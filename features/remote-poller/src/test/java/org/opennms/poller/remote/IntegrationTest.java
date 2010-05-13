package org.opennms.poller.remote;

import java.io.File;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.JUnitHttpServerExecutionListener;
import org.opennms.core.test.annotations.JUnitHttpServer;
import org.opennms.core.test.annotations.Webapp;
import org.opennms.netmgt.dao.db.JUnitTemporaryDatabase;
import org.opennms.netmgt.dao.db.OpenNMSConfigurationExecutionListener;
import org.opennms.netmgt.dao.db.TemporaryDatabaseExecutionListener;
import org.opennms.netmgt.model.OnmsMonitoringLocationDefinition;
import org.opennms.netmgt.poller.remote.PollerFrontEnd;
import org.opennms.test.mock.MockLogAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    OpenNMSConfigurationExecutionListener.class,
    TemporaryDatabaseExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    JUnitHttpServerExecutionListener.class
})
@ContextConfiguration(locations={
    "classpath:/META-INF/opennms/applicationContext-dao.xml",
    "classpath:/META-INF/opennms/applicationContext-daemon.xml",
    "classpath:/META-INF/opennms/mockEventIpcManager.xml",
    "classpath:/META-INF/opennms/applicationContext-setupIpLike-enabled.xml",
    "classpath:/META-INF/opennms/applicationContext-pollerBackEnd.xml",
    "classpath:/META-INF/opennms/applicationContext-remotePollerBackEnd-http.xml",
    "classpath:/META-INF/opennms/applicationContext-pollerFrontEnd.xml",
    "classpath*:/META-INF/opennms/component-dao.xml",
    "classpath:/applicationContext-integration.xml"
})
@JUnitTemporaryDatabase()
@JUnitHttpServer(port=9162, webapps=@Webapp(context="/", path="src/test/resources/integration-test-webapp"))
public class IntegrationTest {
    @Autowired
    private PollerFrontEnd m_frontEnd;

//    @Autowired
//    private PollerBackEnd m_backEnd;

    @BeforeClass
    public static void setUp() {
        Properties p = new Properties();
        p.put("log4j.logger.org.mortbay", "ERROR");
        p.put("log4j.logger.org.quartz", "ERROR");
        p.put("log4j.logger.org.opennms.netmgt.config", "WARN");
        p.put("log4j.logger.org.opennms.netmgt.dao", "WARN");
        MockLogAppender.setupLogging(p);

        File userHome = new File("target");
        System.setProperty("user.home", userHome.getAbsolutePath());
        System.setProperty("user.home.url", userHome.toURI().toString());
        System.setProperty("opennms.poller.server.url", "http://localhost:9162");
    }

    @Test
    @Transactional
    public void testHttpFrontEnd() throws Exception {
        System.err.println("starting");
        for (OnmsMonitoringLocationDefinition def : m_frontEnd.getMonitoringLocations()) {
            System.err.println("found def: " + def);
        }
        System.err.println("finishing");
    }

}
