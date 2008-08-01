package org.opennms.web.rest;

import javax.servlet.ServletContextEvent;

import org.opennms.netmgt.config.DataSourceFactory;
import org.opennms.netmgt.mock.MockDatabase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

import junit.framework.TestCase;

public abstract class AbstractSpringJerseyRestTestCase extends TestCase {

    static String GET = "GET";
    static String POST = "POST";
    static String DELETE = "DELETE";
    static String PUT = "PUT";
    
    String contextPath = "/springrest";
    
    private ServletContainer dispatcher;
    private MockServletConfig servletConfig;
    private MockServletContext servletContext;
    private ContextLoaderListener contextListener;

    public void setUp() throws Exception {
        String userDir = System.getProperty("user.dir");
        System.setProperty("opennms.home", userDir+"/src/test/opennms-home");
        System.setProperty("rrd.base.dir", "/tmp");
        System.setProperty("rrd.binary", "rrdtool");
           
        MockDatabase db = new MockDatabase(true);
        DataSourceFactory.setInstance(db);
                
        servletContext = new MockServletContext("file:src/main/webapp");

        servletContext.addInitParameter("contextConfigLocation", 
                "classpath:/org/opennms/web/rest/applicationContext-test.xml " +
                "classpath:/org/opennms/web/svclayer/applicationContext-svclayer.xml " +
                "classpath:/META-INF/opennms/applicationContext-reporting.xml " +
                "/WEB-INF/applicationContext-acegi-security.xml " +
                "/WEB-INF/applicationContext-jersey.xml");
        
        servletContext.addInitParameter("parentContextKey", "daoContext");
                
        ServletContextEvent e = new ServletContextEvent(servletContext);
        contextListener = new ContextLoaderListener();
        contextListener.contextInitialized(e);
        
        servletContext.setContextPath(contextPath);
        servletConfig = new MockServletConfig(servletContext, "dispatcher");
        
        servletConfig.addInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        servletConfig.addInitParameter("com.sun.jersey.config.property.packages", "org.opennms.web.rest");
    
        dispatcher = new SpringServlet();
        dispatcher.init(servletConfig);
        System.err.println("------------------------------------------------------------------------------");
    }
    
    public void tearDown() {
        System.err.println("------------------------------------------------------------------------------");
        contextListener.contextDestroyed(new ServletContextEvent(servletContext));
        dispatcher.destroy();
    }
    
    protected void dispatch(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
        dispatcher.service(request, response);
    }
    
    protected MockHttpServletResponse createResponse() {
        return new MockHttpServletResponse();
    }

    protected MockHttpServletRequest createRequest(String requestType, String urlPath) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, requestType, contextPath + urlPath);
        request.setContextPath(contextPath);
        return request;
    }
}
