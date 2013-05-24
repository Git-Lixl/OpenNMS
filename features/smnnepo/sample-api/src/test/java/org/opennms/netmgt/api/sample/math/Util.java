package org.opennms.netmgt.api.sample.math;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleProcessor;
import org.opennms.netmgt.api.sample.SampleProcessorBuilder;
import org.opennms.netmgt.api.sample.Timestamp;
import org.opennms.netmgt.api.sample.Results.Row;


public abstract class Util {

	static class TestAdapter extends SampleProcessor {
		Resource m_resource;
		Collection<Metric> m_metrics;
		Iterator<Row> m_iterator;

		public TestAdapter(Results results) {
			m_resource = results.getResource();
			m_metrics = results.getMetrics();
			m_iterator = results.iterator();
		}

		@Override
		public boolean hasNext() {
			return m_iterator.hasNext();
		}

		@Override
		public Row next() {
			return m_iterator.next();
		}

		@Override
		public Collection<Metric> getMetrics() {
			return m_metrics;
		}

		@Override
		public Resource getResource() {
			return m_resource;
		}
	}

	Results find(Resource resource, Timestamp start, Timestamp end, SampleProcessorBuilder bldr, Metric metric) {
		TestAdapter adapter = new TestAdapter(testData(300, TimeUnit.SECONDS, start, end, resource, metric));

		bldr.prepend(adapter);

		SampleProcessor processor = bldr.getProcessor();

		Results results = new Results(resource, metric);
		while(processor.hasNext()) {
			Row row = processor.next();
			for(Sample sample : row) {
				results.addSample(sample);
			}
		}

		return results;
	}

	abstract Results testData(int step, TimeUnit unit, Timestamp start, Timestamp end, Resource resource, Metric... metrics);

	static void printResults(Iterable<Row> r) {
		for (Results.Row row : r) {
			System.out.printf("%s (%d): ", row.getTimestamp(), row.getTimestamp().asMillis());
			for(Sample s : row) {
				System.out.printf("%s:%f", s.getMetric().getName(), s.getValue());
			}
			System.out.printf("%n");
		}
	}
}
