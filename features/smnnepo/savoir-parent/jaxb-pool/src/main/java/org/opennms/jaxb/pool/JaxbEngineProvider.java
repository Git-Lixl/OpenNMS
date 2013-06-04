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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxbEngineProvider implements JaxbEngine {

    private static final Logger LOG = LoggerFactory.getLogger(JaxbEngineProvider.class);

    // Our master jaxb context, this is re-used in several other modules.
    // It contains both WS-Notify and overloaded Subscription classes as well
    // as the WCS schemas.

    private static JAXBContext jaxbContext;

    // The marshallers and unmarshallers are not threadsafe in a shared environment,
    // so that we always try and return a marshaller from the existing pool at all times.

    PooledMarshaller marshaller;
    PooledUnmarshaller unMarshaller;

    private static String jaxbIntroFile = "/annotations.xml";
    private String packageList;
    private List<Class> sizableContextClassesList = new ArrayList<Class>();

    public void setPackageList(String packageList) {
        this.packageList = packageList;
    }

    public JaxbEngineProvider(String packageList) {
        setPackageList(packageList);
        initEngine();
    }

    @Override
    public JAXBContext getContext() {
        LOG.debug("A JaxbContext was requested");
        return jaxbContext;
    }

    private void initEngine() {
        try {
            // Introduce the additional JAXBIntros

            //JaxbIntros config = IntroductionsConfigParser.parseConfig(JaxbEngineProvider.class.getResourceAsStream(jaxbIntroFile));
            //IntroductionsAnnotationReader reader = new IntroductionsAnnotationReader(config);
            //Map<String, Object> jaxbConfig = new HashMap<String, Object>();

            //jaxbConfig.put(JAXBRIContext.ANNOTATION_READER, reader);

            String[] packages = packageList.split(",");
            for(String pkg : packages){
                Class clazz = Class.forName(pkg.trim() + ".ObjectFactory");
                sizableContextClassesList.add(clazz);
            }

            //sizableContextClassesList.add(ExceptionReportMsg.class);

            jaxbContext = JAXBContext.newInstance(getContextClasses());

        } catch (JAXBException e) {
            LOG.error("Cannot initialize the master JAXB Context : ", e);
        } catch (ClassNotFoundException cnfe){
            throw new RuntimeException(cnfe);
        }
        //Setup the pooling
        ObjectPool marshallPool = new StackObjectPool(new MarshallerPool(jaxbContext));
        marshaller = new PooledMarshaller(PoolUtils.erodingPool(marshallPool));

        ObjectPool unMarshallPool = new StackObjectPool(new UnmarshallerPool(jaxbContext));
        unMarshaller = new PooledUnmarshaller(PoolUtils.erodingPool(unMarshallPool));
    }

    public Class[] getContextClasses() {
        return sizableContextClassesList.toArray(new Class[sizableContextClassesList.size()]);
    }

    //Marshalling

    @Override
    public void marshal(Object o, ContentHandler contentHandler) throws JAXBException {
        marshaller.marshal(o, contentHandler);
    }

    @Override
    public void marshal(Object o, Result result) throws JAXBException {
        marshaller.marshal(o, result);
    }

    @Override
    public void marshal(Object o, OutputStream outputStream) throws JAXBException {
        marshaller.marshal(o, outputStream);
    }

    @Override
    public void marshal(Object o, File file) throws JAXBException {
        marshaller.marshal(o, file);
    }

    @Override
    public void marshal(Object o, Writer writer) throws JAXBException {
        marshaller.marshal(o, writer);
    }

    @Override
    public void marshal(Object o, Node node) throws JAXBException {
        marshaller.marshal(o, node);
    }

    @Override
    public void marshal(Object o, XMLStreamWriter xmlStreamWriter) throws JAXBException {
        marshaller.marshal(o, xmlStreamWriter);
    }

    @Override
    public void marshal(Object o, XMLEventWriter xmlEventWriter) throws JAXBException {
        marshaller.marshal(o, xmlEventWriter);
    }

    //Unmarshalling

    @Override
    public Object unmarshal(File file) throws JAXBException {
        return unMarshaller.unmarshal(file);
    }

    @Override
    public Object unmarshal(InputStream inputStream) throws JAXBException {
        return unMarshaller.unmarshal(inputStream);
    }

    @Override
    public Object unmarshal(Reader reader) throws JAXBException {
        return unMarshaller.unmarshal(reader);
    }

    @Override
    public Object unmarshal(URL url) throws JAXBException {
        return unMarshaller.unmarshal(url);
    }

    @Override
    public Object unmarshal(InputSource inputSource) throws JAXBException {
        return unMarshaller.unmarshal(inputSource);
    }

    @Override
    public Object unmarshal(Node node) throws JAXBException {
        return unMarshaller.unmarshal(node);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> tClass) throws JAXBException {
        return unMarshaller.unmarshal(node, tClass);
    }

    @Override
    public Object unmarshal(Source source) throws JAXBException {
        return unMarshaller.unmarshal(source);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> tClass) throws JAXBException {
        return unMarshaller.unmarshal(source, tClass);
    }

    @Override
    public Object unmarshal(XMLStreamReader xmlStreamReader) throws JAXBException {
        return unMarshaller.unmarshal(xmlStreamReader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLStreamReader xmlStreamReader, Class<T> tClass) throws JAXBException {
        return unMarshaller.unmarshal(xmlStreamReader, tClass);
    }

    @Override
    public Object unmarshal(XMLEventReader xmlEventReader) throws JAXBException {
        return unMarshaller.unmarshal(xmlEventReader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLEventReader xmlEventReader, Class<T> tClass) throws JAXBException {
        return unMarshaller.unmarshal(xmlEventReader, tClass);
    }
}
