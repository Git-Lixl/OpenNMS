package org.opennms.netmgt.api.sample;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class CounterValueTest {

	@Test
	public void testWrap() {
		testWrap(64);
	}

	@Test
	public void testWrap32() {
		testWrap(32);
	}

	private void testWrap(int bits) {
		if (bits != 32 && bits != 64) throw new IllegalArgumentException();

		BigInteger maxN = BigInteger.valueOf(2L).pow(bits).subtract(BigInteger.ONE);

		// Start with a value 10 less than the roll-over ceiling 
		CounterValue lastV = new CounterValue(maxN.subtract(BigInteger.TEN));
		CounterValue currV = null;

		// Increment the faux-counter 20x, the last 10 should occur after a roll-over
		for (int i=0; i < 20; i++) {

			// Simulate counter roll-over.
			if (lastV.equals(maxN)) { 
				currV = new CounterValue(BigInteger.ZERO);
			}
			else {
				currV = lastV.add(BigInteger.ONE);
			}

			assertEquals(1L, currV.delta(lastV).longValue());

			lastV = currV;
		}
	}

	@Test
	public void testComposeDecompose() {
		CounterValue v1 = new CounterValue(123456789L);
		assertEquals(v1, CounterValue.compose(CounterValue.decompose(v1)));	
	}

}
