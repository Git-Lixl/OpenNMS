package org.opennms.netmgt.provision.service.puppet;

import org.opennms.core.utils.url.GenericURLConnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * <p>LegacyLocalReportsDaoTest class.</p>
 *
 * @author Ronny Trommer <ronny@opennms.org>
 * @version $Id: $
 * @since 1.8.1
 */
public class PuppetRequisitionUrlConnection extends GenericURLConnection {
    
    private String m_hostname;
    
    private String m_username;
    
    private String m_password;

    private static Map<String, String> m_args;

    protected PuppetRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);
        m_hostname = url.getHost();
        m_username = getUsername(url);
        m_password = getPassword(url);

        m_args = getQueryArgs(url);

        m_foreignSource = "vmware-" + m_hostname;
    }

    private

    /**
     * We have to override this method, we do not really handle a URL connection.
     *
     * @throws IOException
     */
    @Override
    public void connect() throws IOException {
        // We do nothing here, cause we do not really open a connection
    }
}
