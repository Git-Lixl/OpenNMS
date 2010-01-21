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

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.Task;
import org.opennms.protocols.rt.Messenger;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgRequest;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseCallback;
import org.opennms.sms.reflector.smsservice.MobileMsgTrackerImpl;
import org.opennms.sms.reflector.smsservice.SmsResponse;
import org.opennms.sms.reflector.smsservice.UssdResponse;
import org.smslib.InboundMessage;
import org.smslib.USSDDcs;
import org.smslib.USSDResponse;
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

	/**
     * @author brozow
     *
     */
    public class TestMessenger implements Messenger<MobileMsgRequest, MobileMsgResponse> {
        
        Queue<MobileMsgResponse> m_q;

        /* (non-Javadoc)
         * @see org.opennms.protocols.rt.Messenger#sendRequest(java.lang.Object)
         */
        public void sendRequest(MobileMsgRequest request) throws Exception {
            // fake send this
            request.setSendTimestamp(System.currentTimeMillis());
        }

        /* (non-Javadoc)
         * @see org.opennms.protocols.rt.Messenger#start(java.util.Queue)
         */
        public void start(Queue<MobileMsgResponse> q) {
            m_q = q;
        }
        
        public void sendTestResponse(MobileMsgResponse response) {
            m_q.offer(response);
        }

        /**
         * @param msg1
         */
        public void sendTestResponse(InboundMessage msg) {
            sendTestResponse(new SmsResponse(msg, System.currentTimeMillis()));
        }

        /**
         * @param response
         */
        public void sendTestResponse(String gatewayId, USSDResponse response) {
            sendTestResponse(new UssdResponse(gatewayId, response, System.currentTimeMillis()));
        }

    }
    
    public static class TestCallback implements MobileMsgResponseCallback {
        
        CountDownLatch m_latch = new CountDownLatch(1);
        AtomicReference<MobileMsgResponse> m_response = new AtomicReference<MobileMsgResponse>(null);

        
        MobileMsgResponse getResponse() throws InterruptedException {
            m_latch.await();
            return m_response.get();
        }

        /* (non-Javadoc)
         * @see org.opennms.sms.reflector.smsservice.SmsResponseCallback#handleError(org.opennms.sms.reflector.smsservice.SmsRequest, java.lang.Throwable)
         */
        public void handleError(MobileMsgRequest request, Throwable t) {
            System.err.println("Error processing SmsRequest: " + request);
            m_latch.countDown();
        }

        /* (non-Javadoc)
         * @see org.opennms.sms.reflector.smsservice.SmsResponseCallback#handleResponse(org.opennms.sms.reflector.smsservice.SmsRequest, org.opennms.sms.reflector.smsservice.SmsResponse)
         */
        public boolean handleResponse(MobileMsgRequest request, MobileMsgResponse response) {
            m_response.set(response);
            m_latch.countDown();
            return true;
        }

        /* (non-Javadoc)
         * @see org.opennms.sms.reflector.smsservice.SmsResponseCallback#handleTimeout(org.opennms.sms.reflector.smsservice.SmsRequest)
         */
        public void handleTimeout(MobileMsgRequest request) {
            System.err.println("Timeout waiting for SmsRequest: " + request);
            m_latch.countDown();
        }

        /**
         * @return
         * @throws InterruptedException 
         */
        public InboundMessage getMessage() throws InterruptedException {
            MobileMsgResponse response = getResponse();
            if (response instanceof SmsResponse) {
                return ((SmsResponse)response).getMessage();
            }
            return null;
            
        }
        
        public USSDResponse getUSSDResponse() throws InterruptedException{
            MobileMsgResponse response = getResponse();
            if (response instanceof UssdResponse) {
                return ((UssdResponse)response).getMessage();
            }
            return null;
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
        
        MobileSequenceSession session = new MobileSequenceSession();
        session.setTimeout(1000L);
        session.setRetries(0);

        MobileSequenceConfig sequenceConfig = new MobileSequenceConfig();
        
        MobileSequenceTransaction transaction = new MobileSequenceTransaction();
        
        
        SmsSequenceRequest request = new SmsSequenceRequest();
        request.setGatewayId("*");
        request.setRecipient(PHONE_NUMBER);
        request.setText("ping");
        
        SequenceResponseMatcher pongMatcher = new TextResponseMatcher("^[Pp]ong$");
        
        SmsSequenceResponse response = new SmsSequenceResponse();
        response.addMatcher(pongMatcher);
        
        transaction.setRequest(request);
        
        transaction.addResponse(response);
        
        sequenceConfig.addTransaction(transaction);
        
        LatencyCallback cb = new LatencyCallback(start);
 
        Async<MobileMsgResponse> async = request.createAsync(sequenceConfig, transaction, session, m_tracker);
        
        Task t = m_coordinator.createTask(null, async, cb);
        t.schedule();
        
        InboundMessage responseMsg = createInboundMessage(PHONE_NUMBER, "pong");
        
        Thread.sleep(500);
        m_messenger.sendTestResponse(responseMsg);
        
        t.waitFor();

        assertNotNull(cb.getLatency());
        System.err.println("testRawSmsPing(): latency = " + cb.getLatency());
    }

    
    @Test
    public void testRawUssdMessage() throws Exception {
        final String gatewayId = "G";
        
        MobileSequenceSession session = new MobileSequenceSession();
        session.setTimeout(3000L);
        session.setRetries(0);

        MobileSequenceConfig sequenceConfig = new MobileSequenceConfig();
        
        UssdSequenceRequest request = new UssdSequenceRequest();
        request.setText("#225#");
        
        UssdSequenceResponse response = new UssdSequenceResponse();
        response.addMatcher(new TextResponseMatcher(TMOBILE_USSD_MATCH));
        
        MobileSequenceTransaction transaction = new MobileSequenceTransaction();
        transaction.setRequest(request);
        transaction.addResponse(response);
        
        sequenceConfig.addTransaction(transaction);

        LatencyCallback cb = new LatencyCallback(System.currentTimeMillis());
        Async<MobileMsgResponse> async = request.createAsync(sequenceConfig, transaction, session, m_tracker);

        Task t = m_coordinator.createTask(null, async, cb);
        t.schedule();
        
        USSDResponse r = new USSDResponse();
        r.setContent(TMOBILE_RESPONSE);
        r.setUSSDSessionStatus(USSDSessionStatus.NO_FURTHER_ACTION_REQUIRED);
        r.setDcs(USSDDcs.UNSPECIFIED_7BIT);
        
        Thread.sleep(500);
        m_messenger.sendTestResponse(gatewayId, r);
        
        t.waitFor();
        assertNotNull(cb.getLatency());
        System.err.println("testRawUssdMessage(): latency = " + cb.getLatency());
    }

    /**
     * @param originator
     * @param text
     * @return
     */
    private InboundMessage createInboundMessage(String originator, String text) {
        InboundMessage msg = new InboundMessage(new Date(), originator, text, 0, "0");
        return msg;
    }
    

}
