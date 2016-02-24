/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.service.odl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opennms.core.spring.BeanUtils;
import org.opennms.core.utils.url.GenericURLConnection;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.integrations.odl.NamingUtils;
import org.opennms.integrations.odl.OpendaylightRestconfClient;
import org.opennms.integrations.odl.topo.Link;
import org.opennms.integrations.odl.topo.Links;
import org.opennms.netmgt.provision.persist.ForeignSourceRepository;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionMonitoredService;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL format:
 *   odl://HOSTNAME_OR_IP_ADDRESS[/foreignSource-Name][?parameters]
 *
 * where possible parameters are:
 *
 * @author jwhite
 */
public class OpendaylightRequisitionUrlConnection extends GenericURLConnection {
    private static final Logger LOG = LoggerFactory.getLogger(OpendaylightRequisitionUrlConnection.class);

    public static final String PROTOCOL = "odl";

    private final String m_host;
    private final int m_port;
    private final String m_foreignSource;

    private ForeignSourceRepository m_foreignSourceRepository;

    public OpendaylightRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);

        m_host = url.getHost();
        m_port = url.getPort() > 0 ? url.getPort() : OpendaylightRestconfClient.DEFAULT_PORT;

        String path = url.getPath();
        path = path.replaceAll("^/", "");
        path = path.replaceAll("/$", "");
        final String[] pathElements = path.split("/");
        if (pathElements.length != 1) {
            throw new MalformedURLException("Error processing path element of URL: " + url.toExternalForm());
        }
        m_foreignSource = pathElements[0];
    }

    @Override
    public void connect() throws IOException {
        // pass
    }

    private Requisition getExistingRequisition() {
        try {
            if (m_foreignSourceRepository == null) {
                m_foreignSourceRepository = BeanUtils.getBean("daoContext", "deployedForeignSourceRepository", ForeignSourceRepository.class);
            }
            return m_foreignSourceRepository.getRequisition(m_foreignSource);
        } catch (Exception e) {
            LOG.warn("Can't retrieve requisition. {}. Assuming there was no previous requisition.", m_foreignSource);
            return null;
        }
    }

    public static Links getLinksSourceFrom(Topology topology, Node node) {
        return new Links(topology.getLink().stream()
            .filter(l -> node.getNodeId().equals(l.getSource().getSourceNode()))
            .map(l -> new Link(l))
            .collect(Collectors.toList()));
    }

    private Requisition getRequisition() throws Exception {
        LOG.debug("Retrieving existing requisition.");
        Requisition requisition = getExistingRequisition();
        if (requisition == null) {
            LOG.info("No existing requisition was found. Creating a new requisition.");
            requisition = new Requisition();
            requisition.setForeignSource(m_foreignSource);
        }

        final OpendaylightRestconfClient odlClient = new OpendaylightRestconfClient(m_host, m_port);
        final NetworkTopology networkTopology = odlClient.getOperationalNetworkTopology();

        for (Topology topology : networkTopology.getTopology()) {
            final String topologyId = topology.getTopologyId().getValue();
            for (Node node : topology.getNode()) {
                final String nodeId = node.getNodeId().getValue();

                final String foreignId = String.format("%s-%s", topologyId, nodeId)
                        .replaceAll(":", "_"); // Colons are typically used a separators, so we replace them to be safe
                // TODO: What if the string already contains an underscore or colon dash

                // Parse the topology info we need to persist
                Links links = getLinksSourceFrom(topology, node);

                RequisitionAsset requisitionAssetTopologyInfo = new RequisitionAsset("vmwareTopologyInfo", JaxbUtils.marshal(links));
                RequisitionNode requisitionNode = requisition.getNode(foreignId);
                if (requisitionNode != null) {
                    // There already a node in the requisition for this fid
                    // Update the topology info
                    requisitionNode.putAsset(requisitionAssetTopologyInfo);
                    continue;
                }

                requisitionNode = new RequisitionNode();
                requisitionNode.putAsset(requisitionAssetTopologyInfo);

                requisitionNode.setForeignId(foreignId);
                requisitionNode.setNodeLabel(nodeId);

                RequisitionInterface requisitionIf = new RequisitionInterface();
                requisitionIf.setIpAddr(NamingUtils.generateIpAddressForForeignId(foreignId));

                RequisitionMonitoredService requisitionSvc = new RequisitionMonitoredService();
                requisitionSvc.setServiceName("IsAttachedToODLController");

                requisitionIf.getMonitoredServices().add(requisitionSvc);
                requisitionNode.getInterfaces().add(requisitionIf);
                requisition.getNodes().add(requisitionNode);
            }
        }

        return requisition;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            final Requisition requisition = getRequisition();
            return new ByteArrayInputStream(JaxbUtils.marshal(requisition).getBytes());
        } catch (Exception e) {
            throw new IOException("Failed to generate requisition.", e);
        }
    }

    public void setForeignSourceRepository(ForeignSourceRepository foreignSourceRepository) {
        m_foreignSourceRepository = foreignSourceRepository;
    }
}
