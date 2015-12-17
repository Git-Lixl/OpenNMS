/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.api.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.opennms.core.network.InetAddressXmlAdapter;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.web.rest.api.support.JAXBResourceLocationAdapter;
import org.opennms.web.rest.api.support.JsonResourceLocationDeserializationProvider;
import org.opennms.web.rest.api.support.JsonResourceLocationSerializationProvider;
import org.opennms.web.rest.api.support.NodeCriteriaBuilder;
import org.opennms.web.rest.api.support.ResourceLocationFactory;

@XmlRootElement(name = "ipInterface")
@XmlAccessorType(XmlAccessType.NONE)
public class IpInterfaceDTO implements Serializable {

    private ResourceLocation location;
    private ResourceLocation nodeLocation;
    private ResourceLocation servicesLocation;
    private String id;
    private InetAddress ipAddress;
    private String ipHostName;
    private String isManaged;
    private Date ipLastCapsdPoll;
    private Integer ifIndex;
    private String snmpType;
    private boolean isDown;
    private Integer nodeId;


    public IpInterfaceDTO() {
    }

    public IpInterfaceDTO(OnmsIpInterface onmsIpInterface) {
        Objects.requireNonNull(onmsIpInterface);
        this.id = onmsIpInterface.getInterfaceId();
        this.ipAddress = onmsIpInterface.getIpAddress();
        this.ipHostName = onmsIpInterface.getIpHostName();
        this.isManaged = onmsIpInterface.getIsManaged();
        this.ifIndex = onmsIpInterface.getIfIndex();
        this.snmpType = onmsIpInterface.getPrimaryString();
        this.isDown = onmsIpInterface.isDown();
        if (onmsIpInterface.getNodeId() != null) {
            String nodeCriteria = NodeCriteriaBuilder.getNodeCriteria(onmsIpInterface.getNode());
            nodeLocation = ResourceLocationFactory.createNodeLocation(nodeCriteria);
            nodeId = onmsIpInterface.getNodeId();
            if (ipAddress != null) {
                location = ResourceLocationFactory.createIpInterfaceLocation(nodeCriteria, ipAddress.toString());
                servicesLocation = ResourceLocationFactory.createIpServiceLocation(nodeCriteria, ipAddress.toString());
            }
        }
    }

    @XmlID
    @XmlAttribute(name="id")
    @JsonProperty(value="id")
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @XmlAttribute(name="ifIndex")
    @JsonProperty(value="ifIndex")
    public Integer getIfIndex() {
        return ifIndex;
    }

    public void setIfIndex(Integer ifindex) {
        this.ifIndex = ifindex;
    }

    @XmlElement(name="hostName")
    public String getIpHostName() {
        return ipHostName;
    }

    public void setIpHostName(String iphostname) {
        ipHostName = iphostname;
    }

    @XmlAttribute(name="isManaged")
    @JsonProperty(value="isManaged")
    public String getIsManaged() {
        return isManaged;
    }

    public void setIsManaged(String isManaged) {
        this.isManaged = isManaged;
    }

    @XmlElement(name="lastCapsdPoll")
    public Date getIpLastCapsdPoll() {
        return ipLastCapsdPoll;
    }

    public void setIpLastCapsdPoll(Date iplastcapsdpoll) {
        ipLastCapsdPoll = iplastcapsdpoll;
    }

    @XmlAttribute(name="snmpType")
    @JsonProperty(value="snmpType")
    public String getPrimaryString() {
        return snmpType;
    }

    public void setPrimaryString(String snmpPrimary) {
        this.snmpType = snmpPrimary;
    }

    @XmlElement(name="ipAddress")
    @XmlJavaTypeAdapter(InetAddressXmlAdapter.class)
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipaddr) {
        ipAddress = ipaddr;
    }


    @XmlAttribute(name="isDown")
    @JsonProperty(value="isDown")
    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    @XmlElement(name="location")
    @XmlJavaTypeAdapter(JAXBResourceLocationAdapter.class)
    @JsonSerialize(using = JsonResourceLocationSerializationProvider.class)
    @JsonDeserialize(using = JsonResourceLocationDeserializationProvider.class)
    public ResourceLocation getLocation() {
        return location;
    }

    @XmlElement(name="nodeLocation")
    @XmlJavaTypeAdapter(JAXBResourceLocationAdapter.class)
    @JsonSerialize(using = JsonResourceLocationSerializationProvider.class)
    @JsonDeserialize(using = JsonResourceLocationDeserializationProvider.class)
    public ResourceLocation getNodeLocation() {
        return nodeLocation;
    }

    @XmlElement(name="servicesLocation")
    @XmlJavaTypeAdapter(JAXBResourceLocationAdapter.class)
    @JsonSerialize(using = JsonResourceLocationSerializationProvider.class)
    @JsonDeserialize(using = JsonResourceLocationDeserializationProvider.class)
    public ResourceLocation getServicesLocation() {
        return servicesLocation;
    }

    @Deprecated
    @XmlElement(name="nodeId")
    public Integer getNodeId() {
        return nodeId;
    }

    @Deprecated
    public void setNodeInt(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public void setNodeLocation(ResourceLocation nodeLocation) {
        this.nodeLocation = nodeLocation;
    }

    public void setServicesLocation(ResourceLocation servicesLocation) {
        this.servicesLocation = servicesLocation;
    }
}

