package org.opennms.netmgt.api.sample.math;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.opennms.netmgt.api.sample.Agent;
import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleProcessor;
import org.opennms.netmgt.api.sample.SampleProcessorBuilder;
import org.opennms.netmgt.api.sample.Timestamp;
import org.opennms.netmgt.api.sample.Results.Row;


public class RateTest extends Util {
	private int m_step = 300;
	private TimeUnit m_timeUnits = TimeUnit.SECONDS;


	@Test
	public void testNanSamples() {
		Agent agent = new Agent(new InetSocketAddress("127.0.0.1", 161), "SNMP", "localhost");
		final Resource resource = new Resource(agent, "type", "name");
		final Timestamp ts = Timestamp.now();
		final Metric metric = new Metric("ifInOctets", MetricType.COUNTER, "mib2-interfaces");
		final Metric unsampled = new Metric("ifOutOctets", MetricType.COUNTER, "mib2-interfaces");

		@SuppressWarnings("serial")
		SampleProcessor entrance = new SampleProcessor() {
			// Two rows with one sample each, for one metric
			List<Row> rows = new ArrayList<Row>() {{
				Row r = new Row(resource, ts);
				r.addSample(new Sample(resource, metric, r.getTimestamp(), 0.0d));
				add(r);

				r = new Row(resource, ts.plus(300, TimeUnit.SECONDS));
				r.addSample(new Sample(resource, metric, r.getTimestamp(), 300.0d));
				add(r);
			}};

			Iterator<Row> rowsIter = rows.iterator();

			// getMetrics() returns a metric for which no samples exist (ifOutOctets)
			@Override
			public Collection<Metric> getMetrics() {
				return new ArrayList<Metric>() {{
					add(metric);
					add(unsampled);
				}};
			}

			@Override
			public Row next() {
				return rowsIter.next();
			}

			@Override
			public boolean hasNext() {
				return rowsIter.hasNext();
			}
		};

		SampleProcessorBuilder chain = new SampleProcessorBuilder().append(entrance).append(new Rate());
		Iterator<Row> results = chain.getProcessor();

		Row test = results.next();

		/*
		 * Expected: ifInOctets (metric) should be NaN in the first row (the
		 * first row is always NaN with Rate), and 1.0 in the second
		 * ((300.0 / 300s) = 1); ifOutOctets (unsampled) should be present in
		 * both rows, but NaN.
		 */

		assertEquals(Double.NaN, test.getSample(metric).getValue(), 0.0d);
		assertEquals(Double.NaN, test.getSample(unsampled).getValue(), 0.0d);

		test = results.next();

		assertEquals(1.0d, test.getSample(metric).getValue(), 0.0d);
		assertEquals(Double.NaN, test.getSample(unsampled).getValue(), 0.0d);

		// Iterator should be dry at this point.
		assertTrue(!results.hasNext());
	}

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

		printResults(testData(start, end, resource, metric));

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
	Results testData(Timestamp start, Timestamp end, Resource resource, Metric... metrics) {
		Results testData = new Results(resource, metrics);
		Timestamp current = start;

		for (int i=0; current.lessThan(end); i++) {
			for(int j = 0; j < metrics.length; j++) {
				testData.addSample(new Sample(resource, metrics[j], current, i*(j+1)*(new Double(m_step))));
			}
			current = current.plus(m_step, m_timeUnits);
		}

		return testData;
	}

	private static void assertSample(Sample sample, double value) {
		assertEquals(sample.getValue(), value, 0.0d);
	}
}
