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

package org.apache.xmlbeans;

/**
 * Corresponds to the XML Schema
 * <a target="_blank" href="http://www.w3.org/TR/xmlschema-2/#token">xs:token</a> type.
 * One of the derived types based on <a target="_blank" href="http://www.w3.org/TR/xmlschema-2/#string">xs:string</a>.
 * <p>
 * A token is XML's best representation for a "whitespace insensitive string."
 * All carriage returns, linefeeds, and tabs are converted to ordinary space
 * characters (as with <a target="_blank" href="http://www.w3.org/TR/xmlschema-2/#normalizedString">xs:normalizedString</a>),
 * and furthermore, all contiguous runs of space are collapsed to single spaces,
 * and leading and trailing spaces are trimmed.
 * <p>
 * If you want <code>"&nbsp;&nbsp;high&nbsp;&nbsp;priority&nbsp;&nbsp;"</code>
 * to be equivalent to <code>"high priority"</code>, you should consider
 * using xs:token or a subtype of xs:token.
 * <p>
 * When the {@link #getStringValue()} is obtained from an XmlToken, the normalized,
 * trimmed, whitespace collapsed value is returned.
 * <p>
 * Convertible to {@link String}.
 */
public interface XmlToken extends XmlNormalizedString {
    /**
     * The constant {@link SchemaType} object representing this schema type.
     */
    SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_token");

    /**
     * A class with methods for creating instances
     * of {@link XmlToken}.
     */
    final class Factory {
        /**
         * Creates an empty instance of {@link XmlToken}
         */
        public static XmlToken newInstance() {
            return (XmlToken) XmlBeans.getContextTypeLoader().newInstance(type, null);
        }

        /**
         * Creates an empty instance of {@link XmlToken}
         */
        public static XmlToken newInstance(org.apache.xmlbeans.XmlOptions options) {
            return (XmlToken) XmlBeans.getContextTypeLoader().newInstance(type, options);
        }

        /**
         * Creates an immutable {@link XmlToken} value
         */
        public static XmlToken newValue(Object obj) {
            return (XmlToken) type.newValue(obj);
        }

        /**
         * Parses a {@link XmlToken} fragment from a String. For example: "<code>&lt;xml-fragment&gt;string to collapse&lt;/xml-fragment&gt;</code>".
         */
        public static XmlToken parse(java.lang.String s) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(s, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from a String. For example: "<code>&lt;xml-fragment&gt;string to collapse&lt;/xml-fragment&gt;</code>".
         */
        public static XmlToken parse(java.lang.String s, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(s, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from a File.
         */
        public static XmlToken parse(java.io.File f) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(f, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from a File.
         */
        public static XmlToken parse(java.io.File f, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(f, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from a URL.
         */
        public static XmlToken parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(u, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from a URL.
         */
        public static XmlToken parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(u, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from an InputStream.
         */
        public static XmlToken parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(is, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from an InputStream.
         */
        public static XmlToken parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(is, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from a Reader.
         */
        public static XmlToken parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(r, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from a Reader.
         */
        public static XmlToken parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(r, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from a DOM Node.
         */
        public static XmlToken parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(node, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from a DOM Node.
         */
        public static XmlToken parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(node, type, options);
        }

        /**
         * Parses a {@link XmlToken} fragment from an XMLStreamReader.
         */
        public static XmlToken parse(javax.xml.stream.XMLStreamReader xsr) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(xsr, type, null);
        }

        /**
         * Parses a {@link XmlToken} fragment from an XMLStreamReader.
         */
        public static XmlToken parse(javax.xml.stream.XMLStreamReader xsr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlToken) XmlBeans.getContextTypeLoader().parse(xsr, type, options);
        }

        private Factory() {
            // No instance of this class allowed
        }
    }
}

