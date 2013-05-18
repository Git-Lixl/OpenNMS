package org.opennms.netmgt.sample.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.api.sample.Agent;
import org.opennms.netmgt.api.sample.AgentRepository;
import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.api.sample.MetricRepository;
import org.opennms.netmgt.api.sample.Resource;
import org.opennms.netmgt.api.sample.Results;
import org.opennms.netmgt.api.sample.Results.Row;
import org.opennms.netmgt.api.sample.Sample;
import org.opennms.netmgt.api.sample.SampleProcessorBuilder;
import org.opennms.netmgt.api.sample.SampleRepository;
import org.opennms.netmgt.api.sample.Timestamp;
import org.opennms.netmgt.api.sample.math.Rate;

@Path("/samples") 
@Produces(MediaType.APPLICATION_JSON) 
public class SampleResource {
	private SampleRepository m_sampleRepository;
	private AgentRepository<?> m_agentRepository;
	private MetricRepository m_metricRepository;

	@GET
	@Path("{agentId}/{resourceType}/{resourceName}/{metric}")
	public String getSamples(@PathParam("agentId") String agentId, @PathParam("resourceType") String resourceType,
			@PathParam("resourceName") String resourceName, @PathParam("metric") String metricNames,
			@QueryParam("start") String start, @QueryParam("end") String end) {

		Agent agent = m_agentRepository.getAgentById(agentId);

		// SNMP:127.0.0.1:161|ifIndex|wlan0-84:3a:4b:0e:89:94
		Resource r = new Resource(agent, resourceType, resourceName);
		Timestamp endTs = Timestamp.now();
		Timestamp startTs = endTs.minus(6, TimeUnit.MINUTES); 
		
		// Matt made me do this!
		List<Metric> metrics = new ArrayList<Metric>();
		
		for (String metricName : metricNames.split(",")) {
			metrics.add(m_metricRepository.getMetric(metricName));
		}
		
		SampleProcessorBuilder bldr = new SampleProcessorBuilder();
		bldr.append(new Rate());
		
		Results results = m_sampleRepository.find(bldr, startTs, endTs, r, metrics.toArray(new Metric[0]));
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
				double value = sample.getValue();
				if (Double.isNaN(value) || Double.isInfinite(value)) {
					sb.append("null");
				} else {
					sb.append(value);
				}


				sb.append("]");
			}
		}
		
		sb.append(']');
		
		return sb.toString();
	}

	public void setSampleRepository(SampleRepository sampleRepo) {
		m_sampleRepository = sampleRepo;
	}

	public void setAgentRepository(AgentRepository<?> agentRepository) {
		m_agentRepository = agentRepository;
	}

	public void setMetricRepository(MetricRepository metricRepository) {
		m_metricRepository = metricRepository;
	}
}
