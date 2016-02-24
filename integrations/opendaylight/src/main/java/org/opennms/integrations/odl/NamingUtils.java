package org.opennms.integrations.odl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingUtils {

    public static String getTopologyIdFromForeignId(String foreignId) {
        return foreignId.split("-")[0].replace("_", ":");
    }

    public static String getNodeIdFromForeignId(String foreignId) {
        return foreignId.split("-")[1].replace("_", ":");
    }

    public static String generateIpAddressForForeignId(String foreignId) {
        // TODO Generate IPv6 addresses in the link-local prefix fe80::/10 
        Pattern switches = Pattern.compile(".*openflow_(\\d+)$");
        Matcher m = switches.matcher(foreignId);
        if (m.find()) {
            return "127.0.10." + m.group(1);
        }
        
        Pattern hosts = Pattern.compile(".*host_00_00_00_00_00_0(\\d+)$");
        m = hosts.matcher(foreignId);
        if (m.find()) {
            return "127.0.20." + m.group(1);
        }

        throw new RuntimeException("Unsupported fid: " + foreignId);
    }
}
