package org.opennms.netmgt.sample.graph;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.api.sample.Agent;
import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricType;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Results.Row;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleRepository;
import org.opennms.netmgt.api.sample.Timestamp;

@Path("/samples") @Produces(MediaType.APPLICATION_JSON) public class SampleResource {
	@SuppressWarnings("unused") private SampleRepository m_sampleRepository;

	@GET
	@Path("{agentId}/{resourceType}/{resourceName}/{metric}")
	public String getSamples(@PathParam("agentId") String agentId, @PathParam("resourceType") String resourceType,
			@PathParam("resourceName") String resourceName, @PathParam("metric") String metric,
			@QueryParam("start") String start, @QueryParam("end") String end) {

		// SNMP:127.0.0.1:161|ifIndex|wlan0-84:3a:4b:0e:89:94
		Resource r = new Resource(
				new Agent(new InetSocketAddress("127.0.0.1", 161), "SNMP"),
				"ifIndex",
				"wlan0-84:3a:4b:0e:89:94");
		Timestamp endTs = Timestamp.now();
		Timestamp startTs = new Timestamp((endTs.asSeconds() - 360), TimeUnit.SECONDS);
		Metric m1 = new Metric("ifHCInOctets", MetricType.COUNTER, "mib2-interfaces");
		Metric m2 = new Metric("ifHCOutOctets", MetricType.COUNTER, "mib2-interfaces");
		
		Results results = m_sampleRepository.find(startTs, endTs, r, m1);
		StringBuilder sb = new StringBuilder();
		boolean first = false;
		int i = 0;

		sb.append('[').append("\n");
		
		for (Row row : results) {
			i++;
			if ((i % 10) != 0) {
				continue;
			}
			
			for (Metric met : results.getMetrics()) {
				if (!first) {
					sb.append("  [");
					first = true;
				}
				else {
					sb.append(",\n [");
				}
				
				Sample sample = row.getSample(met);
				sb.append('"').append(met.getName()).append('"').append(',');
				sb.append(sample.getTimestamp().asMillis()).append(',');
				sb.append(sample.getValue());

				sb.append("]");
			}
		}
		
		sb.append(']');
		
		return sb.toString();
	}

	public void setSampleRepository(SampleRepository sampleRepo) {
		m_sampleRepository = sampleRepo;
	}
}
