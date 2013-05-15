package org.opennms.netmgt.sampler.config.snmp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.opennms.netmgt.api.sample.AgentRepository;

public class SnmpAgentRepository implements AgentRepository<SnmpAgent> {
	private List<SnmpAgent> m_agents = new CopyOnWriteArrayList<SnmpAgent>();
	
	public void addAgent(SnmpAgent agent) {
		m_agents.add(agent);
	}

	@Override
	public List<SnmpAgent> getAgentsByProtocol(String protocol) {
		return m_agents;
	}

}
