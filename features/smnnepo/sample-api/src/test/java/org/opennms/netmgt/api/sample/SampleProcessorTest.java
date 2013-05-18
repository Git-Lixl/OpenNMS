package org.opennms.netmgt.api.sample;

import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.opennms.netmgt.api.sample.Results.Row;
import org.opennms.netmgt.api.sample.math.Rate;

public class SampleProcessorTest {

	public class Noop extends SampleProcessor {

		@Override
		public boolean hasNext() {
			return getProcessor().hasNext();
		}

		@Override
		public Row next() {
			return getProcessor().next();
		}



	}
	

	public class Avg extends SampleProcessor {
		@Override
		public boolean hasNext() {
			return getProcessor().hasNext();
		}

		@Override
		public Row next() {
			Row r = getProcessor().next();
			
			Row newRow = new Row(r.getTimestamp());
			for(Sample s : r) {
				Sample newSample = new Sample(s.getResource(), s.getMetric(), s.getTimestamp(), 7.0);
				newRow.addSample(newSample);
			}
			return newRow;
		}
	}
	@Test
	public void test() {
		
		Agent agent = new Agent(new InetSocketAddress("127.0.01", 161), "SNMP", "localhost");
		Resource resource = new Resource(agent, "type", "name");
		Metric metric = new Metric("inOctets", MetricType.COUNTER, "mib2-interfaces");
		
		printResults(testData(resource, metric));
		
		System.out.println("---------------");

		SampleProcessorBuilder bldr = new SampleProcessorBuilder();
		bldr.append(new Rate());

		Results results = find(resource, Timestamp.now(), Timestamp.now(), bldr, metric);
		//
		
		assertNotNull(results);
		assertNotNull(results.getRows());
		
		printResults(results);
		
		
		
	}
	
	private static void printResults(Results r) {
		for (Results.Row row : r.getRows()) {
			System.out.printf("%s (%d): ", row.getTimestamp(), row.getTimestamp().asMillis());
			for(Sample s : row) {
				System.out.printf("%s:%f", s.getMetric().getName(), s.getValue());
			}
			System.out.printf("%n");
		}
	}
	private class TestAdapter extends SampleProcessor {
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

	private Results find(Resource resource, Timestamp start, Timestamp end, SampleProcessorBuilder bldr, Metric metric) {
		
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

	private Results testData(Resource resource, Metric... metrics) {
		return testData(5, 300, TimeUnit.SECONDS, resource, metrics);
	}
	
	private Results testData(int sampleCount, int step, TimeUnit unit, Resource resource, Metric... metrics) {
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

}
