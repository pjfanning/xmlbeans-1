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

import java.util.Calendar;


/**
 * Corresponds to the XML Schema
 * <a target="_blank" href="http://www.w3.org/TR/xmlschema-2/#time">xs:time</a> type.
 * A gDay specifies only a day-of-month.
 * <p>
 * Convertible to {@link Calendar} or {@link GDate}.
 *
 * @see XmlCalendar
 * @see GDate
 */
public interface XmlTime extends XmlAnySimpleType {
    /**
     * The constant {@link SchemaType} object representing this schema type.
     */
    SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_time");

    /**
     * Returns this value as a {@link Calendar}
     */
    Calendar getCalendarValue();

    /**
     * Sets this value as a {@link Calendar}
     */
    void setCalendarValue(Calendar c);

    /**
     * Returns this value as a {@link GDate}
     */
    GDate getGDateValue();

    /**
     * Sets this value as a {@link GDateSpecification}
     */
    void setGDateValue(GDate gd);

    /**
     * A class with methods for creating instances
     * of {@link XmlTime}.
     */
    final class Factory {
        /**
         * Creates an empty instance of {@link XmlTime}
         */
        public static XmlTime newInstance() {
            return (XmlTime) XmlBeans.getContextTypeLoader().newInstance(type, null);
        }

        /**
         * Creates an empty instance of {@link XmlTime}
         */
        public static XmlTime newInstance(org.apache.xmlbeans.XmlOptions options) {
            return (XmlTime) XmlBeans.getContextTypeLoader().newInstance(type, options);
        }

        /**
         * Creates an immutable {@link XmlTime} value
         */
        public static XmlTime newValue(Object obj) {
            return (XmlTime) type.newValue(obj);
        }

        /**
         * Parses a {@link XmlTime} fragment from a String. For example: "<code>&lt;xml-fragment&gt;12:00:00&lt;/xml-fragment&gt;</code>".
         */
        public static XmlTime parse(java.lang.String s) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(s, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from a String. For example: "<code>&lt;xml-fragment&gt;12:00:00&lt;/xml-fragment&gt;</code>".
         */
        public static XmlTime parse(java.lang.String s, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(s, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from a File.
         */
        public static XmlTime parse(java.io.File f) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(f, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from a File.
         */
        public static XmlTime parse(java.io.File f, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(f, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from a URL.
         */
        public static XmlTime parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(u, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from a URL.
         */
        public static XmlTime parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(u, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from an InputStream.
         */
        public static XmlTime parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(is, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from an InputStream.
         */
        public static XmlTime parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(is, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from a Reader.
         */
        public static XmlTime parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(r, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from a Reader.
         */
        public static XmlTime parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(r, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from a DOM Node.
         */
        public static XmlTime parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(node, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from a DOM Node.
         */
        public static XmlTime parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(node, type, options);
        }

        /**
         * Parses a {@link XmlTime} fragment from an XMLStreamReader.
         */
        public static XmlTime parse(javax.xml.stream.XMLStreamReader xsr) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(xsr, type, null);
        }

        /**
         * Parses a {@link XmlTime} fragment from an XMLStreamReader.
         */
        public static XmlTime parse(javax.xml.stream.XMLStreamReader xsr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (XmlTime) XmlBeans.getContextTypeLoader().parse(xsr, type, options);
        }

        private Factory() {
            // No instance of this class allowed
        }
    }
}

