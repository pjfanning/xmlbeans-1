/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.binding.bts.BindingLoader;
import org.apache.xmlbeans.impl.binding.bts.BindingType;
import org.apache.xmlbeans.impl.binding.bts.BindingTypeName;
import org.apache.xmlbeans.impl.binding.bts.BuiltinBindingLoader;
import org.apache.xmlbeans.impl.binding.bts.BuiltinBindingType;
import org.apache.xmlbeans.impl.binding.bts.ByNameBean;
import org.apache.xmlbeans.impl.binding.bts.JavaTypeName;
import org.apache.xmlbeans.impl.binding.bts.SimpleBindingType;
import org.apache.xmlbeans.impl.binding.bts.XmlTypeName;
import org.apache.xmlbeans.impl.common.ConcurrentReaderHashMap;
import org.apache.xmlbeans.impl.common.XmlWhitespace;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;

/**
 * Table of TypeMarshaller and TypeUnmarshaller objects keyed by BindingType
 */
final class RuntimeBindingTypeTable
{
    private final Map unmarshallerMap;
    private final Map marshallerMap;
    private final RuntimeTypeFactory runtimeTypeFactory;

    private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    private static final ConcurrentReaderHashMap BUILTIN_MARSHALLER_MAP;
    private static final ConcurrentReaderHashMap BUILTIN_UNMARSHALLER_MAP;

    static
    {
        final RuntimeBindingTypeTable tbl =
            new RuntimeBindingTypeTable(null);
        tbl.addBuiltins();
        BUILTIN_UNMARSHALLER_MAP = (ConcurrentReaderHashMap)tbl.unmarshallerMap;
        BUILTIN_MARSHALLER_MAP = (ConcurrentReaderHashMap)tbl.marshallerMap;
    }

    static RuntimeBindingTypeTable createTable(RuntimeTypeFactory factory)
    {
        final RuntimeBindingTypeTable tbl =
            new RuntimeBindingTypeTable((Map)BUILTIN_UNMARSHALLER_MAP.clone(),
                                        (Map)BUILTIN_MARSHALLER_MAP.clone(),
                                        factory);
        return tbl;
    }


    private RuntimeBindingTypeTable(Map unmarshallerMap,
                                    Map marshallerMap,
                                    RuntimeTypeFactory runtimeTypeFactory)
    {
        this.unmarshallerMap = unmarshallerMap;
        this.marshallerMap = marshallerMap;
        this.runtimeTypeFactory = runtimeTypeFactory;
    }

    private RuntimeBindingTypeTable(RuntimeTypeFactory runtimeTypeFactory)
    {
        this(new ConcurrentReaderHashMap(),
             new ConcurrentReaderHashMap(),
             runtimeTypeFactory);
    }

    private TypeUnmarshaller createTypeUnmarshaller(BindingType type,
                                                    BindingLoader loader)
        throws XmlException
    {
        TypeUnmarshaller type_um;
        //TODO: cleanup this nasty instanceof stuff (Visitor?)

        if (type instanceof SimpleBindingType) {
            //note this could return a static for builtin types
            type_um = createSimpleTypeUnmarshaller((SimpleBindingType)type,
                                                   loader, this);
        } else if (type instanceof ByNameBean) {
            //NOTE that in the case of cyclical types, it is possible
            //to have "dangling" TypeUnmarshal objects that are not in the map.
            //But this is of no concern as they are immutable and will share
            //the same RuntimeBindingType object
            ByNameRuntimeBindingType runtimeType =
                (ByNameRuntimeBindingType)runtimeTypeFactory.createRuntimeType(type, this, loader);
            type_um = new ByNameUnmarshaller(runtimeType);
        } else {
            throw new AssertionError("UNIMPLEMENTED TYPE: " + type);
        }
        putTypeUnmarshaller(type, type_um);
        type_um.initialize(this, loader);
        return type_um;
    }


    TypeUnmarshaller getOrCreateTypeUnmarshaller(BindingType type,
                                                 BindingLoader loader)
        throws XmlException
    {
        TypeUnmarshaller um = (TypeUnmarshaller)unmarshallerMap.get(type);
        if (um == null) {
            um = createTypeUnmarshaller(type, loader);
        }
        return um;
    }


    TypeUnmarshaller getTypeUnmarshaller(BindingType type)
    {
        return (TypeUnmarshaller)unmarshallerMap.get(type);
    }

    TypeMarshaller getTypeMarshaller(BindingType type)
    {
        return (TypeMarshaller)marshallerMap.get(type);
    }

    private void putTypeUnmarshaller(BindingType type, TypeUnmarshaller um)
    {
        assert type != null;
        assert um != null;

        unmarshallerMap.put(type, um);
    }

    private void putTypeMarshaller(BindingType type, TypeMarshaller m)
    {
        assert type != null;
        assert m != null;

        marshallerMap.put(type, m);
    }

    private void addXsdBuiltin(String xsdType,
                               Class javaClass,
                               TypeConverter converter)
    {
        final BindingLoader bindingLoader = BuiltinBindingLoader.getInstance();

        QName xml_type = new QName(XSD_NS, xsdType);
        JavaTypeName jName = JavaTypeName.forString(javaClass.getName());
        XmlTypeName xName = XmlTypeName.forTypeNamed(xml_type);
        BindingType btype =
            bindingLoader.getBindingType(BindingTypeName.forPair(jName, xName));
        if (btype == null) {
            throw new AssertionError("failed to find builtin for java:" + jName +
                                     " - xsd:" + xName);
        }
        putTypeMarshaller(btype, converter);
        putTypeUnmarshaller(btype, converter);

        assert getTypeMarshaller(btype) == converter;
        assert getTypeUnmarshaller(btype) == converter;
    }


    private void addBuiltins()
    {
        final FloatTypeConverter float_conv = new FloatTypeConverter();
        addXsdBuiltin("float", float.class, float_conv);
        addXsdBuiltin("float", Float.class, float_conv);

        final DoubleTypeConverter double_conv = new DoubleTypeConverter();
        addXsdBuiltin("double", double.class, double_conv);
        addXsdBuiltin("double", Double.class, double_conv);

        final IntegerTypeConverter integer_conv = new IntegerTypeConverter();
        final Class bigint = BigInteger.class;
        addXsdBuiltin("integer", bigint, integer_conv);
        addXsdBuiltin("nonPositiveInteger", bigint, integer_conv);
        addXsdBuiltin("negativeInteger", bigint, integer_conv);
        addXsdBuiltin("nonNegativeInteger", bigint, integer_conv);
        addXsdBuiltin("positiveInteger", bigint, integer_conv);
        addXsdBuiltin("unsignedLong", bigint, integer_conv);

        addXsdBuiltin("decimal", BigDecimal.class,
                      new DecimalTypeConverter());

        final LongTypeConverter long_conv = new LongTypeConverter();
        addXsdBuiltin("long", long.class, long_conv);
        addXsdBuiltin("long", Long.class, long_conv);
        addXsdBuiltin("unsignedInt", long.class, long_conv);
        addXsdBuiltin("unsignedInt", Long.class, long_conv);

        final IntTypeConverter int_conv = new IntTypeConverter();
        addXsdBuiltin("int", int.class, int_conv);
        addXsdBuiltin("int", Integer.class, int_conv);
        addXsdBuiltin("unsignedShort", int.class, int_conv);
        addXsdBuiltin("unsignedShort", Integer.class, int_conv);

        final ShortTypeConverter short_conv = new ShortTypeConverter();
        addXsdBuiltin("short", short.class, short_conv);
        addXsdBuiltin("short", Short.class, short_conv);
        addXsdBuiltin("unsignedByte", short.class, short_conv);
        addXsdBuiltin("unsignedByte", Short.class, short_conv);

        final ByteTypeConverter byte_conv = new ByteTypeConverter();
        addXsdBuiltin("byte", byte.class, byte_conv);
        addXsdBuiltin("byte", Byte.class, byte_conv);

        final BooleanTypeConverter boolean_conv = new BooleanTypeConverter();
        addXsdBuiltin("boolean", boolean.class, boolean_conv);
        addXsdBuiltin("boolean", Boolean.class, boolean_conv);

        final StringTypeConverter string_conv = new StringTypeConverter();
        final Class str = String.class;
        addXsdBuiltin("string", str, string_conv);
        addXsdBuiltin("normalizedString", str, string_conv);
        addXsdBuiltin("token", str, string_conv);
        addXsdBuiltin("language", str, string_conv);
        addXsdBuiltin("Name", str, string_conv);
        addXsdBuiltin("NCName", str, string_conv);
        addXsdBuiltin("NMTOKEN", str, string_conv);
        addXsdBuiltin("ID", str, string_conv);
        addXsdBuiltin("IDREF", str, string_conv);
        addXsdBuiltin("ENTITY", str, string_conv);

        addXsdBuiltin("anyURI",
                      str,
                      new AnyUriToStringTypeConverter());

        addXsdBuiltin("dateTime",
                      Calendar.class,
                      new DateTimeTypeConverter());

        addXsdBuiltin("QName",
                      QName.class,
                      new QNameTypeConverter());
    }

    private static TypeUnmarshaller createSimpleTypeUnmarshaller(SimpleBindingType stype,
                                                                 BindingLoader loader,
                                                                 RuntimeBindingTypeTable table)
        throws XmlException
    {
        TypeUnmarshaller um = table.getTypeUnmarshaller(stype);
        if (um != null) return um;

        int curr_ws = XmlWhitespace.WS_UNSPECIFIED;
        SimpleBindingType curr = stype;
        BuiltinBindingType resolved;

        while (true) {
            //we want to keep the first whitespace setting as we walk up
            if (curr_ws == XmlWhitespace.WS_UNSPECIFIED) {
                curr_ws = curr.getWhitespace();
            }

            BindingTypeName asif_name = curr.getAsIfBindingTypeName();
            if (asif_name != null) {
                BindingType asif_new = loader.getBindingType(asif_name);
                if (asif_new instanceof BuiltinBindingType) {
                    resolved = (BuiltinBindingType)asif_new;
                    break;
                } else if (asif_new instanceof SimpleBindingType) {
                    curr = (SimpleBindingType)asif_new;
                } else {
                    String msg = "invalid as-xml type: " + asif_name +
                        " on type: " + curr.getName();
                    throw new XmlException(msg);
                }
            } else {
                throw new XmlException("missing as-xml type on " +
                                       curr.getName());
            }
        }
        assert resolved != null;


        //special processing for whitespace facets.
        //TODO: assert that our type is derived from xsd:string
        switch (curr_ws) {
            case XmlWhitespace.WS_UNSPECIFIED:
                break;
            case XmlWhitespace.WS_PRESERVE:
                return PreserveStringTypeConverter.getInstance();
            case XmlWhitespace.WS_REPLACE:
                return ReplaceStringTypeConverter.getInstance();
            case XmlWhitespace.WS_COLLAPSE:
                return CollapseStringTypeConverter.getInstance();
            default:
                throw new AssertionError("invalid whitespace: " + curr_ws);
        }


        um = table.getTypeUnmarshaller(resolved);
        if (um != null) return um;

        String msg = "unable to get simple type unmarshaller for " + stype +
            " resolved to " + resolved;
        throw new AssertionError(msg);
    }

}