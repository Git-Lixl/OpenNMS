package org.opennms.netmgt.api.sample.math;

import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;

import org.junit.Test;
import org.opennms.netmgt.api.sample.Agent;
import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.SampleProcessorBuilder;
import org.opennms.netmgt.api.sample.Timestamp;


public class RateTest {

	@Test
	public void test() {
		Agent agent = new Agent(new InetSocketAddress("127.0.0.1", 161), "SNMP", "localhost");
		Resource resource = new Resource(agent, "type", "name");
		Metric metric = new Metric("inOctets", MetricType.COUNTER, "mib2-interfaces");

		Util.printResults(Util.testData(resource, metric));

		System.out.println("---------------");

		SampleProcessorBuilder bldr = new SampleProcessorBuilder();
		bldr.append(new Rate());

		Results results = Util.find(resource, Timestamp.now(), Timestamp.now(), bldr, metric);

		assertNotNull(results);
		assertNotNull(results.getRows());

		Util.printResults(results);
	}
}
