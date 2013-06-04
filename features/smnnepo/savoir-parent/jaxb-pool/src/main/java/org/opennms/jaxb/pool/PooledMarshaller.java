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
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PooledMarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(PooledMarshaller.class);
    private ObjectPool pool;

    public PooledMarshaller(ObjectPool pool) {
        this.pool = pool;
    }

    public void marshal(Object o, OutputStream os) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, os);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, Result res) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, res);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, File file) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, file);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, Writer writer) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, writer);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, ContentHandler contentHandler) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, contentHandler);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, Node node) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, node);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, XMLStreamWriter xmlStreamWriter) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, xmlStreamWriter);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public void marshal(Object o, XMLEventWriter xmlEventWriter) {

        Marshaller marshaller = null;
        try {
            marshaller = (Marshaller) (pool.borrowObject());
            marshaller.marshal(o, xmlEventWriter);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != marshaller) {
                    pool.returnObject(marshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }
}
