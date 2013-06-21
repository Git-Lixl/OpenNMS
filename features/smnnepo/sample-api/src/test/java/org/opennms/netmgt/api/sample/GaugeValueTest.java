package org.opennms.netmgt.api.sample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GaugeValueTest {

	@Test
	public void testDeltas() {
		assertEquals(2.5d, new GaugeValue(2.5d).delta(5.0d).doubleValue(), 0.0d);
		assertEquals(2.5d, new GaugeValue(5.0d).delta(2.5d).doubleValue(), 0.0d);
	}

	@Test
	public void testComposeDecompose() {
		GaugeValue v1 = new GaugeValue(12345.6789d);
		assertEquals(v1, GaugeValue.compose(GaugeValue.decompose(v1)));	
	}
}
