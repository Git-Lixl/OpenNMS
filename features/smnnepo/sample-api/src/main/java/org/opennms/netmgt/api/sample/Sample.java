package org.opennms.netmgt.api.sample;

import java.io.Serializable;

public class Sample implements Comparable<Sample>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Resource  m_resource;
	private final Metric    m_metric;
	private final Timestamp m_timestamp;
	private final double    m_value;
	
	
	public Sample(Resource resource, Metric metric, Timestamp timestamp, double value) {
		m_resource = resource;
		m_metric = metric;
		m_timestamp = timestamp;
		m_value = value;
	}


	public Resource getResource() {
		return m_resource;
	}


	public Metric getMetric() {
		return m_metric;
	}


	public Timestamp getTimestamp() {
		return m_timestamp;
	}


	public double getValue() {
		return m_value;
	}


	@Override
	public int compareTo(Sample o) {
		return getTimestamp().compareTo(o.getTimestamp());
	}


	@Override
	public String toString() {
		return String.format("%s %s:%s=%f", m_timestamp, m_resource.getIdentifier(), m_metric.getName(), m_value);
	}



}
