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
import java.io.Reader;
import java.net.URL;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PooledUnmarshaller {
    
    private static final Logger LOG = LoggerFactory.getLogger(PooledUnmarshaller.class);

    private ObjectPool pool;

    public PooledUnmarshaller(ObjectPool pool) {
        this.pool = pool;
    }

    public Object unmarshal(File file) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(file);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(InputStream inputStream) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(Reader reader) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(reader);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(URL url) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(url);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(InputSource inputSource) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(inputSource);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(Node node) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(node);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public <T> javax.xml.bind.JAXBElement<T> unmarshal(Node node, Class<T> tClass) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(node, tClass);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(Source source) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(source);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public <T> javax.xml.bind.JAXBElement<T> unmarshal(Source source, Class<T> tClass) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(source, tClass);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(XMLStreamReader xmlStreamReader) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(xmlStreamReader);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public <T> javax.xml.bind.JAXBElement<T> unmarshal(XMLStreamReader xmlStreamReader, Class<T> tClass) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(xmlStreamReader, tClass);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public Object unmarshal(XMLEventReader xmlEventReader) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(xmlEventReader);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }

    public <T> javax.xml.bind.JAXBElement<T> unmarshal(XMLEventReader xmlEventReader, Class<T> tClass) {
        Unmarshaller unMarshaller = null;
        try {
            unMarshaller = (Unmarshaller) (pool.borrowObject());
            return unMarshaller.unmarshal(xmlEventReader, tClass);
        } catch (Exception e) {
            LOG.error("Could not marshal code ", e);
        } finally {
            try {
                if (null != unMarshaller) {
                    pool.returnObject(unMarshaller);
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return null;
    }
}
