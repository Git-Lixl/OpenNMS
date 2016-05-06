package org.opennms.netmgt.snmp.proxy.camel;

import java.math.BigInteger;
import java.net.InetAddress;

import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpResult;
import org.opennms.netmgt.snmp.SnmpValue;
import org.opennms.netmgt.snmp.proxy.common.SnmpResponseDTO;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activactor implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        // Prime the JAXBContext
        final SnmpResult result = new SnmpResult(
                SnmpObjId.get(".1.3.6.1.2"),
                new SnmpInstId(".1.3.6.1.2.1.4.34.1.3.1.2.3.4"),
                new SnmpValue() {

                    @Override
                    public boolean isEndOfMib() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean isError() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean isNull() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean isDisplayable() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean isNumeric() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public int toInt() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public String toDisplayString() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public InetAddress toInetAddress() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public long toLong() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public BigInteger toBigInteger() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String toHexString() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public int getType() {
                        return 1;
                    }

                    @Override
                    public byte[] getBytes() {
                        return new byte[] {0x1};
                    }

                    @Override
                    public SnmpObjId toSnmpObjId() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                });
        final SnmpResponseDTO walkResult = new SnmpResponseDTO();
        walkResult.getResults().add(result);
        JaxbUtils.marshal(walkResult);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // pass
    }
}
