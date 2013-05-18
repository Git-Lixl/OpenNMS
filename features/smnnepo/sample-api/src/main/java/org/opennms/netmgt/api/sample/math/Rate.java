package org.opennms.netmgt.api.sample.math;

import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleProcessor;
import org.opennms.netmgt.api.sample.Timestamp;
import org.opennms.netmgt.api.sample.Results.Row;

public class Rate extends SampleProcessor {
	Row prev;
	@Override
	public boolean hasNext() {
		return getProcessor().hasNext();
	}

	@Override
	public Row next() {
		Row r = getProcessor().next();
		
		Row newRow = new Row(r.getTimestamp());
		for(Sample s : r) {
			Timestamp prevTime = prev == null ? null : prev.getTimestamp();
			double prevVal = prev == null ? Double.NaN : prev.getSample(s.getMetric()).getValue();
			long elapsed = prev == null ? 0 : s.getTimestamp().asMillis() - prevTime.asMillis(); 
			double rate = prev == null ? Double.NaN : 1000.0*(s.getValue() - prevVal) / elapsed;
			Sample newSample = new Sample(s.getResource(), s.getMetric(), s.getTimestamp(), rate);
			newRow.addSample(newSample);
		}
		prev = r;
		return newRow;
	}
	
}