package org.opennms.netmgt.sampler.config.snmp;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.netmgt.api.sample.Metric;

/**
 * 	<group name="mib2-coffee-rfc2325">
 *      <mibObj oid=".1.3.6.1.2.1.10.132.2" instance="0" alias="coffeePotCapacity" type="integer" />
 *      <mibObj oid=".1.3.6.1.2.1.10.132.4.1.2" instance="0" alias="coffeePotLevel" type="integer" />
 *      <mibObj oid=".1.3.6.1.2.1.10.132.4.1.6" instance="0" alias="coffeePotTemp" type="integer" />
 *  </group>
 *  
 * @author brozow
 *
 */
@XmlRootElement(name="group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {
	
	@XmlAttribute(name="name")
	private String m_name;
	
	@XmlElement(name="mibObj")
	private MibObject[] m_mibObjects = new MibObject[0];

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public MibObject[] getMibObjects() {
		return m_mibObjects;
	}

	public void setMibObjects(MibObject[] mibObjects) {
		m_mibObjects = mibObjects;
	}

	public void fillRequest(SnmpCollectionRequest request) {
		request.addGroup(this);
	}

	public String toString() {
		return getName();
	}

	public Set<Metric> getMetrics() {
		Set<Metric> metrics = new HashSet<Metric>();
		for(MibObject mibObj : m_mibObjects) {
			Metric metric = mibObj.createMetric(getName());
			if (metric != null) { metrics.add(metric); }
		}
		return metrics;
	}

	public Metric getMetric(String metricName) {
		for(MibObject mibObj : m_mibObjects) {
			if (mibObj.getAlias().equals(metricName)) {
				return mibObj.createMetric(getName());
			}
		}
		return null;
	}
}
