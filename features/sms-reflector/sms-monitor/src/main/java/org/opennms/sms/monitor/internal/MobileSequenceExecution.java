/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2010 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
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
package org.opennms.sms.monitor.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.internal.config.MobileSequenceTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;

/**
 * MobileSequenceExecution
 *
 * @author brozow
 */
public class MobileSequenceExecution {
    
    private Map<String, Number> m_responseTimes = new HashMap<String,Number>();
    private Long m_startTime;
    private SequenceTask m_task;
    private DefaultTaskCoordinator m_coordinator;

    public MobileSequenceExecution(DefaultTaskCoordinator coordinator) {
        m_coordinator = coordinator;
        createTask();
    }
    
    public DefaultTaskCoordinator getTaskCoordinator() {
        return m_coordinator;
    }

    public Long getStartTime() {
        return m_startTime;
    }
    
    public void setStartTime(Long startTime) {
        m_startTime = startTime;
    }
    
    public Map<String, Number> getResponseTimes() {
        return m_responseTimes;
    }

    public void end() {
        long end = System.currentTimeMillis();
    	getResponseTimes().put("response-time", Long.valueOf(end - (long) getStartTime()));
    }

    public SequenceTask getTask() {
        return m_task;
    }

    public void setTask(SequenceTask task) {
        m_task = task;
    }

    public void start() {
        setStartTime(System.currentTimeMillis());
    
        getTask().schedule();
    }

    public void createTask() {
        SequenceTask sequence = getTaskCoordinator().createSequence(null);
        setTask(sequence);
    }

    public void createTransactionExecution(MobileSequenceSession session, MobileSequenceTransaction transaction) {
        getTask().add(getTaskCoordinator().createTask(getTask(), createAsync(session, transaction), getCallback(transaction)));
    }

    private Async<MobileMsgResponse> createAsync(MobileSequenceSession session, MobileSequenceTransaction transaction) {
        return transaction.createAsync(session);
    }

    private Callback<MobileMsgResponse> getCallback(final MobileSequenceTransaction transaction) {
        return new Callback<MobileMsgResponse>() {
            public void complete(MobileMsgResponse t) {
                if (t != null) {
                    transaction.setLatency((t.getReceiveTime() - t.getRequest().getSentTime()));
                }
                
                //session.setVariable()
            }
        
            public void handleException(Throwable t) {
                transaction.setError(t);
            }
        
        
        };
    }

    public void waitFor()
            throws InterruptedException, ExecutionException {
        Task task = getTask();
        if (task == null) {
            throw new IllegalStateException("waiting for the sequence to comlete but the sequence has never been started!");
        }
        task.waitFor();
        
        end();
    }

}
