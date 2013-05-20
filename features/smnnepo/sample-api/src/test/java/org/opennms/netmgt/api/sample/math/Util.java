package org.opennms.netmgt.api.sample.math;

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


public class Util {

	private static class TestAdapter extends SampleProcessor {
		Iterator<Row> m_iterator;

		public TestAdapter(Results results) {
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
	}

	static Results find(Resource resource, Timestamp start, Timestamp end, SampleProcessorBuilder bldr, Metric metric) {
		TestAdapter adapter = new TestAdapter(testData(resource, metric));

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

	static Results testData(Resource resource, Metric... metrics) {
		return testData(5, 300, TimeUnit.SECONDS, resource, metrics);
	}

	static Results testData(int sampleCount, int step, TimeUnit unit, Resource resource, Metric... metrics) {
		Results testData = new Results(resource, metrics);
		Timestamp now = Timestamp.now().minus(sampleCount*step, unit);
		for(int i = 0; i < sampleCount; i++) {
			Timestamp ts = now.plus(i*step, unit);
			for(int j = 0; j < metrics.length; j++) {
				testData.addSample(new Sample(resource, metrics[j], ts, i*(j+1)*300.0));
			}
		}
		return testData;
	}

	static void printResults(Results r) {
		for (Results.Row row : r.getRows()) {
			System.out.printf("%s (%d): ", row.getTimestamp(), row.getTimestamp().asMillis());
			for(Sample s : row) {
				System.out.printf("%s:%f", s.getMetric().getName(), s.getValue());
			}
			System.out.printf("%n");
		}
	}
}
