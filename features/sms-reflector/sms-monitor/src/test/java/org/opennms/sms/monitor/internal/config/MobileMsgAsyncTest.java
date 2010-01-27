/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2009 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 * OpenNMS Licensing       <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 */
package org.opennms.sms.monitor.internal.config;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.TestMessenger;
import org.opennms.sms.monitor.internal.MobileSequenceConfigBuilder;
import org.opennms.sms.monitor.internal.MobileSequenceConfigBuilder.MobileSequenceTransactionBuilder;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgTrackerImpl;
import org.smslib.USSDSessionStatus;

/**
 * MobileMsgTrackerTeste
 *
 * @author brozow
 */
public class MobileMsgAsyncTest {
    
	private static final String PHONE_NUMBER = "+19195551212";
    public static final String TMOBILE_RESPONSE = "37.28 received on 08/31/09. For continued service through 10/28/09, please pay 79.56 by 09/28/09.    ";
    public static final String TMOBILE_USSD_MATCH = "^.*[\\d\\.]+ received on \\d\\d/\\d\\d/\\d\\d. For continued service through \\d\\d/\\d\\d/\\d\\d, please pay [\\d\\.]+ by \\d\\d/\\d\\d/\\d\\d.*$";

    private final class LatencyCallback implements Callback<MobileMsgResponse> {
		private final AtomicLong m_start = new AtomicLong();
		private final AtomicLong m_end = new AtomicLong();

		private LatencyCallback(long startTime) {
			m_start.set(startTime);
		}

		public void complete(MobileMsgResponse t) {
			if (t != null) {
				m_end.set(System.currentTimeMillis());
			}
		}

		public void handleException(Throwable t) {
		}
		
		public Long getLatency() {
			if (m_end.get() == 0) {
				return null;
			} else {
				return m_end.get() - m_start.get();
			}
		}
	}

	TestMessenger m_messenger;
    MobileMsgTrackerImpl m_tracker;
	DefaultTaskCoordinator m_coordinator;
    
    @Before
    public void setUp() throws Exception {
        m_messenger = new TestMessenger();
        m_tracker = new MobileMsgTrackerImpl("test", m_messenger);
        m_tracker.start();
        
        m_coordinator = new DefaultTaskCoordinator(Executors.newSingleThreadExecutor());

        System.err.println("=== STARTING TEST ===");
    }

    @Test
    public void testRawSmsPing() throws Exception {
        final long start = System.currentTimeMillis();
        
        MobileSequenceSession session = new MobileSequenceSession(m_tracker);
        session.setTimeout(1000L);
        session.setRetries(0);
        
        MobileSequenceConfigBuilder bldr = new MobileSequenceConfigBuilder();

        MobileSequenceTransactionBuilder smsRequest = bldr.smsRequest("SMS ping", "*", PHONE_NUMBER, "ping");
        smsRequest.expectSmsResponse().matching("^[Pp]ong$");
        

        LatencyCallback cb = new LatencyCallback(start);
 
        Async<MobileMsgResponse> async = smsRequest.getTransaction().createAsync(session);
        
        Task t = m_coordinator.createTask(null, async, cb);
        t.schedule();
        
        Thread.sleep(500);

        m_messenger.sendTestResponse(PHONE_NUMBER, "pong");
        
        t.waitFor();

        assertNotNull(cb.getLatency());
        System.err.println("testRawSmsPing(): latency = " + cb.getLatency());
    }

    @Test
    public void testRawUssdMessage() throws Exception {
        final String gatewayId = "G";
        
        MobileSequenceSession session = new MobileSequenceSession(m_tracker);
        session.setTimeout(3000L);
        session.setRetries(0);

        MobileSequenceConfigBuilder bldr = new MobileSequenceConfigBuilder();
        
        MobileSequenceTransactionBuilder transBldr = bldr.ussdRequest("USSD request", "*", "#225#");
        transBldr.expectUssdResponse().matching(TMOBILE_USSD_MATCH);

        LatencyCallback cb = new LatencyCallback(System.currentTimeMillis());
        
        Async<MobileMsgResponse> async = transBldr.getTransaction().createAsync(session);

        Task t = m_coordinator.createTask(null, async, cb);
        t.schedule();
        
        Thread.sleep(500);

        m_messenger.sendTestResponse(gatewayId, TMOBILE_RESPONSE, USSDSessionStatus.NO_FURTHER_ACTION_REQUIRED);
        
        t.waitFor();
        assertNotNull(cb.getLatency());
        System.err.println("testRawUssdMessage(): latency = " + cb.getLatency());
    }
    

}
