package org.apache.xmlbeans.impl.newstore2;

import javax.xml.namespace.QName;

import java.util.ConcurrentModificationException;

import org.apache.xmlbeans.XmlOptions;

import java.io.Writer;
import java.io.Reader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

class Saver
{
    Saver ( Cur c, XmlOptions options )
    {
        _locale = c._locale;
        _version = _locale.version();

        _cur = c.weakCur( this );
        _preProcess = true;

        _namespaceStack = new ArrayList();
        _uriMap = new HashMap();
        _prefixMap = new HashMap();
        _firstPush = true;
        
        // TODO - establish _synthName
    }

    protected final void checkVersion ( )
    {
        if (_version != _locale.version())
            throw new ConcurrentModificationException( "Document changed during save" );
    }

    protected final boolean process ( )
    {
        assert _locale.entered();
        
        checkVersion();

        if (_preProcess)
        {
            assert _cur != null;

            _preProcess = false;

            _done = true;

            if (!_cur.isContainer())
                throw new RuntimeException( "Not implemented" );

            assert _cur.isContainer();

            _done = false;

            _end = _cur.weakCur( this );
            _end.toEnd();

            _top = _cur.weakCur( this );
        }

        // TODO _postPop here 

        if (_postProcess)
        {
            if (_cur.isSamePosition( _end ))
                _done = true;
            else
            {
                Cur prev = _cur.isContainer() ? _cur.tempCur() : null;

                int k = _cur.kind();

                assert
                    k == Cur.ROOT || k == Cur.ELEM
                        || k == Cur.COMMENT || k == Cur.PROCINST ||
                            k == - Cur.ROOT || k == - Cur.ELEM;

                switch ( k )
                {
                case Cur.ROOT :
                case Cur.ELEM :
                {
                    _cur.nextNonAttr();

                    break;
                }

                case - Cur.ROOT :
                case - Cur.ELEM :
                {
                    _cur.next();
                    break;
                }

                case Cur.COMMENT :
                case Cur.PROCINST :
                {   
                    _cur.toEnd();
                    _cur.next();
                    break;
                }
                }
                

                // todo - deal with text after here
                
                Cur.release( prev );
            }
        }
        
        if (_done)
        {
            _cur.release();      _cur = null;
            Cur.release( _end ); _end = null;
            Cur.release( _top ); _top = null;
            
            return false;
        }

        checkVersion();

        _skipContainerFinish = false;

        int k = _cur.kind();
        
        assert
            k == Cur.ROOT || k == Cur.ELEM
                || k == Cur.COMMENT || k == Cur.PROCINST ||
                    k == - Cur.ROOT || k == - Cur.ELEM;
                
        switch ( k )
        {
            case Cur.ROOT :
            case Cur.ELEM :
            {
                processContainer();
                break;
            }
            
            case - Cur.ROOT :
            case - Cur.ELEM :
            {
                processFinish();
                break;
            }
            
            case Cur.COMMENT :
            {
                throw new RuntimeException( "Not implemented" );
            }
            
            case Cur.PROCINST :
            {
                throw new RuntimeException( "Not implemented" );
            }
        }

        _postProcess = true;

        return true;
    }

    private final void processContainer ( )
    {
        assert _cur.isContainer();
        assert !_cur.isRoot() || _cur.getName() == null;
        
        QName name =
            _synthElem != null && _cur.isSamePosition( _top ) ? _synthElem : _cur.getName();

        String nameUri = name == null ? null : name.getNamespaceURI();
        
        // TODO - check for doctype to save out here

        ;

        // Add a new entry to the frontier.  If this element has a name
        // which has no namespace, then we must make sure that pushing
        // the mappings causes the default namespace to be empty

        boolean ensureDefaultEmpty = name != null && nameUri.length() == 0;

        pushMappings( _cur, ensureDefaultEmpty );
    }

    private final void processFinish ( )
    {
        throw new RuntimeException( "Not implemented" );
    }

    //
    // Layout of namespace stack:
    //
    //    URI Undo
    //    URI Rename
    //    Prefix Undo
    //    Mapping
    //

    boolean hasMappings ( )
    {
        int i = _namespaceStack.size();

        return i > 0 && _namespaceStack.get( i - 1 ) != null;
    }

    void iterateMappings ( )
    {
        _currentMapping = _namespaceStack.size();

        while ( _currentMapping > 0 &&
                  _namespaceStack.get( _currentMapping - 1 ) != null )
        {
            _currentMapping -= 8;
        }
    }

    boolean hasMapping ( )
    {
        return _currentMapping < _namespaceStack.size();
    }

    void nextMapping ( )
    {
        _currentMapping += 8;
    }

    String mappingPrefix ( )
    {
        assert hasMapping();
        return (String) _namespaceStack.get( _currentMapping + 6 );
    }

    String mappingUri ( )
    {
        assert hasMapping();
        return (String) _namespaceStack.get( _currentMapping + 7 );
    }

    String mappingPrevPrefixUri ( )
    {
        assert hasMapping();
        return (String) _namespaceStack.get( _currentMapping + 5 );
    }

    private final void pushMappings ( Cur container, boolean ensureDefaultEmpty )
    {
        assert container.isContainer();
        
        _namespaceStack.add( null );

        Cur c = container.tempCur();
        
        for ( boolean C = true ; C ; C = c.toParentRaw() )
        {
            Cur a = c.tempCur();

            namespaces:
            for ( boolean A = a.toFirstAttr() ; A ; A = a.toNextAttr() )
            {
                if (a.isXmlns())
                {
                    String prefix = a.getXmlnsPrefix();
                    String uri = a.getValueString();
                    
                    if (ensureDefaultEmpty && prefix.length() == 0 && uri.length() > 0)
                        continue;
                    
                    // Make sure the prefix is not already mapped in this frame

                    for ( iterateMappings() ; hasMapping() ; nextMapping() )
                        if (mappingPrefix().equals( prefix ))
                            continue namespaces;

                    addMapping( prefix, uri );
                }
            }

            a.release();

            // Push all ancestors the first time
            
            if (!_firstPush)
                break;
        }

        c.release();

        if (ensureDefaultEmpty)
        {
            String defaultUri = (String) _prefixMap.get( "" );

            // I map the default to "" at the very beginning
            assert defaultUri != null;

            if (defaultUri.length() > 0)
                addMapping( "", "" );
        }

        _firstPush = false;
    }
    
    private final void addMapping ( String prefix, String uri )
    {
        assert uri != null;
        assert prefix != null;

        // If the prefix being mapped here is already mapped to a uri,
        // that uri will either go out of scope or be mapped to another
        // prefix.

        String renameUri = (String) _prefixMap.get( prefix );
        String renamePrefix = null;

        if (renameUri != null)
        {
            // See if this prefix is already mapped to this uri.  If
            // so, then add to the stack, but there is nothing to rename
        
            if (renameUri.equals( uri ))
                renameUri = null;
            else
            {
                int i = _namespaceStack.size();

                while ( i > 0 )
                {
                    if (_namespaceStack.get( i - 1 ) == null)
                    {
                        i--;
                        continue;
                    }

                    if (_namespaceStack.get( i - 7 ).equals( renameUri ))
                    {
                        renamePrefix = (String) _namespaceStack.get( i - 8 );

                        if (renamePrefix == null || !renamePrefix.equals( prefix ))
                            break;
                    }

                    i -= 8;
                }

                assert i > 0;
            }
        }

        _namespaceStack.add( _uriMap.get( uri ) );
        _namespaceStack.add( uri );

        if (renameUri != null)
        {
            _namespaceStack.add( _uriMap.get( renameUri ) );
            _namespaceStack.add( renameUri );
        }
        else
        {
            _namespaceStack.add( null );
            _namespaceStack.add( null );
        }

        _namespaceStack.add( prefix );
        _namespaceStack.add( _prefixMap.get( prefix ) );

        _namespaceStack.add( prefix );
        _namespaceStack.add( uri );

        _uriMap.put( uri, prefix );
        _prefixMap.put( prefix, uri );

        if (renameUri != null)
            _uriMap.put( renameUri, renamePrefix );
    }
    
    //
    //
    //

    static final class TextSaver extends Saver
    {
        TextSaver ( Cur c, XmlOptions options, String encoding )
        {
            super( c, options );

            // TODO - do something with encoding here
        }
        
        private int ensure ( int cch )
        {
            // Even if we're asked to ensure nothing, still try to ensure
            // atleast one character so we can determine if we're at the
            // end of the stream.

            if (cch <= 0)
                cch = 1;

            int available = getAvailable();

            for ( ; available < cch ; available = getAvailable() )
                if (!process())
                    break;

            assert available == getAvailable();

            if (available == 0)
                return 0;

            return available;
        }

        int getAvailable ( )
        {
            return _buf == null ? 0 : _buf.length - _free;
        }

        private int resize ( int cch, int i )
        {
            assert _free >= 0;
            assert cch > 0;
            assert cch > _free;

            int newLen = _buf == null ? _initialBufSize : _buf.length * 2;
            int used = getAvailable();

            while ( newLen - used < cch )
                newLen *= 2;

            char[] newBuf = new char [ newLen ];

            if (used > 0)
            {
                if (_in > _out)
                {
                    assert i == -1 || (i >= _out && i < _in);
                    System.arraycopy( _buf, _out, newBuf, 0, used );
                    i -= _out;
                }
                else
                {
                    assert i == -1 || (i >= _out || i < _in);
                    System.arraycopy( _buf, _out, newBuf, 0, used - _in );
                    System.arraycopy( _buf, 0, newBuf, used - _in, _in );
                    i = i >= _out ? i - _out : i + _out;
                }
                
                _out = 0;
                _in = used;
                _free += newBuf.length - _buf.length;
            }
            else
            {
                _free += newBuf.length;
                assert _in == 0 && _out == 0;
                assert i == -1;
            }

            _buf = newBuf;

            assert _free >= 0;

            return i;
        }

        public int read ( )
        {
            if (ensure( 1 ) == 0)
                return -1;

            assert getAvailable() > 0;

            int ch = _buf[ _out ];

            _out = (_out + 1) % _buf.length;
            _free++;

            return ch;
        }

        public int read ( char[] cbuf, int off, int len )
        {
            // Check for end of stream even if there is no way to return
            // characters because the Reader doc says to return -1 at end of
            // stream.

            int n;

            if ((n = ensure( len )) == 0)
                return -1;

            if (cbuf == null || len <= 0)
                return 0;

            if (n < len)
                len = n;

            if (_out < _in)
            {
                System.arraycopy( _buf, _out, cbuf, off, len );
            }
            else
            {
                int chunk = _buf.length - _out;

                if (chunk >= len)
                    System.arraycopy( _buf, _out, cbuf, off, len );
                else
                {
                    System.arraycopy( _buf, _out, cbuf, off, chunk );
                    System.arraycopy( _buf, 0, cbuf, off + chunk, len - chunk );
                }
            }

            _out = (_out + len) % _buf.length;
            _free += len;

            assert _free >= 0;

            return len;
        }

        public int write ( Writer writer, int cchMin )
        {
            while ( getAvailable() < cchMin)
            {
                if (!process())
                    break;
            }

            int charsAvailable = getAvailable();

            if (charsAvailable > 0)
            {
                // I don't want to deal with the circular cases

                assert _out == 0;

                try
                {
                    writer.write( _buf, 0, charsAvailable );
                    writer.flush();
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }

                _free += charsAvailable;
                
                assert _free >= 0;
                
                _in = 0;
            }

            return charsAvailable;
        }

        public String saveToString ( )
        {
            // We're gonna build a string.  Instead of using StringBuffer, may
            // as well use my buffer here.  Fill the whole sucker up and
            // create a String!

            while ( process() )
                ;

            assert _out == 0;

            int available = getAvailable();

            return available == 0 ? "" : new String( _buf, _out, available );
        }

        //
        //
        //

        private static final int _initialBufSize = 4096;

        private int _lastEmitIn;
        private int _lastEmitCch;

        private int    _free;
        private int    _in;
        private int    _out;
        private char[] _buf;
    }
    
    static final class TextReader extends Reader
    {
        TextReader ( Cur c, XmlOptions options )
        {
            _textSaver = new TextSaver( c, options, null );
        }

        public void close ( ) throws IOException { }

        public boolean ready ( ) throws IOException { return true; }

        public int read ( ) throws IOException
        {
            return _textSaver.read();
        }

        public int read ( char[] cbuf ) throws IOException
        {
            return _textSaver.read( cbuf, 0, cbuf == null ? 0 : cbuf.length );
        }

        public int read ( char[] cbuf, int off, int len ) throws IOException
        {
            return _textSaver.read( cbuf, off, len );
        }

        private TextSaver _textSaver;
    }
    
    //
    //
    //

    private final Locale _locale;
    private final long   _version;
    
    private Cur _cur;
    private Cur _end;
    private Cur _top;

    private boolean _preProcess;
    private boolean _postProcess;
    private boolean _done;
    private boolean _skipContainerFinish;

    private QName _synthElem;

    private ArrayList _namespaceStack;
    private int       _currentMapping;
    private boolean   _firstPush;
    private HashMap   _uriMap;
    private HashMap   _prefixMap;
}