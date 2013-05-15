package org.opennms.netmgt.api.sample;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Agent implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String m_protocol;
	
	private final InetSocketAddress m_agentAddress;

	public Agent(InetSocketAddress agentAddress, String protocol) {
		m_agentAddress = agentAddress;
		m_protocol = protocol;
	}
	
	public Agent(InetAddress addr, int port, String protocol) {
		this(new InetSocketAddress(addr, port), protocol);
	}

	public String getProtocol() {
		return m_protocol;
	}

	public InetSocketAddress getAgentAddress() {
		return m_agentAddress;
	}

	public InetAddress getInetAddress() {
		return m_agentAddress.getAddress();
	}
	
	public int getPort() {
		return m_agentAddress.getPort();
	}
	
	public String getName() {
		return m_protocol+":"+ m_agentAddress.getAddress().getHostAddress()+":" + m_agentAddress.getPort();
	}
}
