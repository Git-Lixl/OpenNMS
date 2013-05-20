package org.opennms.netmgt.api.sample.math;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.opennms.netmgt.api.sample.Agent;
import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleProcessorBuilder;
import org.opennms.netmgt.api.sample.Timestamp;


public class RateTest extends Util {

	@Test
	public void test() {
		Agent agent = new Agent(new InetSocketAddress("127.0.0.1", 161), "SNMP", "localhost");
		Resource resource = new Resource(agent, "type", "name");
		Metric metric = new Metric("inOctets", MetricType.COUNTER, "mib2-interfaces");
		int numSamples = 5;
		int stepSeconds = 300;
		Timestamp start = Timestamp.now();
		Timestamp end = start.plus(stepSeconds*numSamples, TimeUnit.SECONDS);

		System.out.println("IN ------------");

		printResults(testData(stepSeconds, TimeUnit.SECONDS, start, end, resource, metric));

		System.out.println();
		System.out.println("OUT -----------");

		SampleProcessorBuilder bldr = new SampleProcessorBuilder();
		bldr.append(new Rate());

		Results results = find(resource, start, end, bldr, metric);

		printResults(results);

		assertNotNull(results);

		// There must be exactly numSamples rows
		assertTrue(results.getRows().size() == numSamples);

		Iterator<Results.Row> rowsIter = results.iterator();

		for (int i=0; rowsIter.hasNext(); i++) {
			Results.Row r = rowsIter.next();

			// There must be only 1 sample
			Collection<Sample> samples = r.getSamples();
			assertEquals(samples.size(), 1);

			// The first sample must be NaN, all subsequent 1.0d
			double expected = (i == 0) ? Double.NaN : 1.0d;
			assertSample(samples.iterator().next(), expected);
		}
	}

	/** Results where rate works out to exactly one for all samples */
	Results testData(int step, TimeUnit unit, Timestamp start, Timestamp end, Resource resource, Metric... metrics) {
		Results testData = new Results(resource, metrics);
		Timestamp current = start;

		for (int i=0; current.lessThan(end); i++) {
			for(int j = 0; j < metrics.length; j++) {
				testData.addSample(new Sample(resource, metrics[j], current, i*(j+1)*(new Double(step))));
			}
			current = current.plus(step, unit);
		}

		return testData;
	}

	private static void assertSample(Sample sample, double value) {
		assertEquals(sample.getValue(), value, 0.0d);
	}
}
