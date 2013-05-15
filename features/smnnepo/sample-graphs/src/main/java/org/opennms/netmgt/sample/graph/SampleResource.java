package org.opennms.netmgt.sample.graph;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.api.sample.SampleRepository;

@Path("/samples")
@Produces(MediaType.APPLICATION_JSON)
public class SampleResource {
//	private SampleRepository sampleRepo;
	
	public void setSampleRepo(SampleRepository sampleRepo) {
//		this.sampleRepo = sampleRepo;
	}
}
