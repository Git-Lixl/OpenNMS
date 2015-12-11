package org.opennms.web.rest.v1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennms.web.rest.api.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("infoRestService")
@Path("info")
@Transactional
public class InfoRestService extends OnmsRestService {
    private static final Logger LOG = LoggerFactory.getLogger(InfoRestService.class);

    private static final String m_displayVersion;
    private static final String m_version;
    static {
        m_displayVersion = System.getProperty("version.display", "");
        final Pattern versionPattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+).*?$");
        final Matcher m = versionPattern.matcher(m_displayVersion);
        if (m.matches()) {
            m_version = m.group(1);
        } else {
            m_version = m_displayVersion;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        Info info = new Info();
        info.setDisplayVersion(m_displayVersion);
        info.setVersion(m_version);

        final InputStream installerProperties = getClass().getResourceAsStream("/installer.properties");
        if (installerProperties != null) {
            final Properties props = new Properties();
            try {
                props.load(installerProperties);
                installerProperties.close();
                info.setPackageName((String)props.get("install.package.name"));
                info.setPackageDescription((String)props.get("install.package.description"));
            } catch (final IOException e) {
                LOG.debug("Unable to read from installer.properties in the classpath.", e);
            }
        }
        return Response.ok().entity(info).build();
    }
}
