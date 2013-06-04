/*
 * =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
 * * The MIT License
 * * ---------------
 * * <p/>
 * * Copyright (c) 2009 University Corporation for Atmospheric Research and Massachusetts Institute of
 * * Technology Lincoln Laboratory
 * * <p/>
 * * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * * associated documentation files (the "Software"), to deal in the Software without restriction,
 * * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * * subject to the following conditions:
 * * <p/>
 * * The above copyright notice and this permission notice shall be included in all copies or substantial
 * * portions of the Software.
 * * <p/>
 * * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
 */

package org.opennms.jaxb.pool;

import javax.xml.bind.JAXBContext;

public interface JaxbEngine {
    /**
     * @return a JAXBContext with all classes needed for WCSRI
     */
    public JAXBContext getContext();

    void marshal(Object o, javax.xml.transform.Result result) throws javax.xml.bind.JAXBException;

    void marshal(Object o, java.io.OutputStream outputStream) throws javax.xml.bind.JAXBException;

    void marshal(Object o, java.io.File file) throws javax.xml.bind.JAXBException;

    void marshal(Object o, java.io.Writer writer) throws javax.xml.bind.JAXBException;

    void marshal(Object o, org.xml.sax.ContentHandler contentHandler) throws javax.xml.bind.JAXBException;

    void marshal(Object o, org.w3c.dom.Node node) throws javax.xml.bind.JAXBException;

    void marshal(Object o, javax.xml.stream.XMLStreamWriter xmlStreamWriter) throws javax.xml.bind.JAXBException;

    void marshal(Object o, javax.xml.stream.XMLEventWriter xmlEventWriter) throws javax.xml.bind.JAXBException;

    //UnMarshalling stuff

    Object unmarshal(java.io.File file) throws javax.xml.bind.JAXBException;

    Object unmarshal(java.io.InputStream inputStream) throws javax.xml.bind.JAXBException;

    Object unmarshal(java.io.Reader reader) throws javax.xml.bind.JAXBException;

    Object unmarshal(java.net.URL url) throws javax.xml.bind.JAXBException;

    Object unmarshal(org.xml.sax.InputSource inputSource) throws javax.xml.bind.JAXBException;

    Object unmarshal(org.w3c.dom.Node node) throws javax.xml.bind.JAXBException;

    <T> javax.xml.bind.JAXBElement<T> unmarshal(org.w3c.dom.Node node, Class<T> tClass) throws javax.xml.bind.JAXBException;

    Object unmarshal(javax.xml.transform.Source source) throws javax.xml.bind.JAXBException;

    <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.transform.Source source, Class<T> tClass) throws javax.xml.bind.JAXBException;

    Object unmarshal(javax.xml.stream.XMLStreamReader xmlStreamReader) throws javax.xml.bind.JAXBException;

    <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.stream.XMLStreamReader xmlStreamReader, Class<T> tClass) throws javax.xml.bind.JAXBException;

    Object unmarshal(javax.xml.stream.XMLEventReader xmlEventReader) throws javax.xml.bind.JAXBException;

    <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.stream.XMLEventReader xmlEventReader, Class<T> tClass) throws javax.xml.bind.JAXBException;

    public Class[] getContextClasses();
}
