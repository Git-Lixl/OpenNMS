package org.opennms.netmgt.api.sample;

import java.util.List;

public interface AgentRepository<T extends Agent> {
	
	void addAgent(T agent);
	
	List<T> getAgentsByProtocol(String protocol);

}
