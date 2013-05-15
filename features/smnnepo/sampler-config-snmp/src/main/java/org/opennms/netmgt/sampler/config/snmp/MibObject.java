package org.opennms.netmgt.sampler.config.snmp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.snmp.SnmpObjId;

/**
 *  <mibObj oid=".1.3.6.1.2.1.10.132.2" instance="0" alias="coffeePotCapacity" type="integer" />
 *  
 * @author brozow
 *
 */
@XmlRootElement(name="mibObj")
@XmlAccessorType(XmlAccessType.FIELD)
public class MibObject {
	
	@XmlAttribute(name="oid")
	@XmlJavaTypeAdapter(SnmpObjIdXmlAdapter.class)
	private SnmpObjId m_oid;
	
	@XmlAttribute(name="alias")
	private String m_alias;
	
	@XmlAttribute(name="type")
	private String m_type;
	
	@XmlAttribute(name="instance")
	private String m_instance;

	public SnmpObjId getOid() {
		return m_oid;
	}

	public void setOid(SnmpObjId oid) {
		m_oid = oid;
	}

	public String getAlias() {
		return m_alias;
	}

	public void setAlias(String alias) {
		m_alias = alias;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		m_type = type;
	}

	public String getInstance() {
		return m_instance;
	}

	public void setInstance(String instance) {
		m_instance = instance;
	}

	public MetricType getMetricType() {
		String type = getType().toLowerCase();
		if (type.startsWith("counter")) {
			return MetricType.COUNTER;
		} else if (type.startsWith("gauge")) {
			return MetricType.GAUGE;
		} else if (type.startsWith("integer")) {
			return MetricType.GAUGE;
		} else {
			return null;
		}
	}

	public Metric createMetric(String groupName) {
		MetricType type = getMetricType();
		if (type == null) return null;
		return new Metric(getAlias(), type, groupName);
	}
	
}
