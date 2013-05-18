package org.opennms.netmgt.api.sample;

import java.util.Iterator;

import org.opennms.netmgt.api.sample.Results.Row;

public abstract class SampleProcessor implements Iterator<Row> {
	private SampleProcessor m_producer;
	
	protected SampleProcessor getProcessor() {
		return getProducer();
	}

	public void setProducer(SampleProcessor producer) {
		m_producer = producer;
	}
	
	public SampleProcessor getProducer() {
			return m_producer;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Iterator<Row>.remove is not yet implemented.");
	}
}
