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
 * Modifications:
 * 
 * Created: August 28, 2009
 *
 * Copyright (C) 2009 The OpenNMS Group, Inc.  All rights reserved.
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
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.netmgt.notifd;

import java.util.List;

import org.apache.log4j.Category;
import org.opennms.core.utils.Argument;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.NotificationManager;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;


/**
 * Send notifications to a TwitterAPI-compatible microblog service.
 * 
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 * @author <a href="mailto:http://www.opennms.org">OpenNMS</a>
 */
public class MicroblogNotificationStrategy implements NotificationStrategy {
    private static final String SERVICE_URL = "serviceUrl";
    private static final String AUTHEN_USERNAME = "authenUser";
    private static final String AUTHEN_PASSWORD = "authenPass";
    private static final String DIRECT_MSG = "directMessage";
    private static final String AT_REPLY = "atReply";
    
    private class StatusOrDM extends TwitterResponse {
        private long m_id;
        private TwitterResponse m_response;
        
        private StatusOrDM(Status status) {
            m_response = status;
            m_id = status.getId();
        }
        
        private StatusOrDM(DirectMessage dm) {
            m_response = dm;
            m_id = dm.getId();
        }
        
        public TwitterResponse getResponse() {
            return m_response;
        }
        
        public long getId() {
            return m_id;
        }
    }
    
    public MicroblogNotificationStrategy() {
    }

    public int send(List<Argument> arguments) {
        Twitter svc = buildUblogService(arguments);
        String messageBody = buildMessageBody(arguments);
        TwitterResponse response;
        
        if (log().isDebugEnabled()) {
            log().debug("Dispatching microblog notification for user '" + svc.getUserId() + "' at base URL '" + svc.getBaseURL() + "' with message '" + messageBody + "'");
        }
        try {
            response = dispatch(arguments, svc, messageBody);
        } catch (TwitterException e) {
            log().error("Microblog notification failed");
            return 1;
        }
        
        log().info("Microblog notification succeeded");
        if (response instanceof Status) {
            log().info("Status update posted with ID=" + ((Status)response).getId());
        } else if (response instanceof DirectMessage) {
            log().info("Direct message sent with ID=" + ((DirectMessage)response).getId());
        }
        
        return 0;
    }
    
    private StatusOrDM dispatch(List<Argument> arguments, Twitter svc, String messageBody) throws TwitterException {
        boolean directMessage = false;
        boolean atReply = false;
        String destName = null;

        for (Argument arg : arguments) {            
            if (DIRECT_MSG.equals(arg.getSwitch())) {
                if ("true".equals(arg.getValue().toLowerCase())) {
                    directMessage = true;
                    if (log().isDebugEnabled()) {
                        log().debug("Directing notification as a direct message");
                    }
                }
            } else if (AT_REPLY.equals(arg.getSwitch())) {
                if ("true".equals(arg.getValue().toLowerCase())) {
                    atReply = true;
                    if (log().isDebugEnabled()) {
                        log().debug("Directing notification as an at-reply");
                    }
                }
            } else if (NotificationManager.PARAM_MICROBLOG_USERNAME.equals(arg.getSwitch())) {
                if (arg.getValue() != null) {

                    destName = arg.getValue().trim();
                    if (destName.startsWith("@")) {
                        destName = destName.substring(1);
                    }
                }
            }
        }
        
        if (atReply && directMessage) {
            throw new IllegalArgumentException("Both atReply and directMessage cannot be set simultaneously");
        } else if (atReply && destName == null) {
            throw new IllegalArgumentException("Cannot send an at-reply to a user whose microblog username is not set");
        } else if (directMessage && destName == null) {
            throw new IllegalArgumentException("Cannot send a direct message to a user whose microblog username is not set");
        }

        if (directMessage) {
            return new StatusOrDM(directMessage(svc, destName, messageBody));
        } else {
            return new StatusOrDM(update(svc, destName, messageBody));
        }
        
    }
    
    private Status update(Twitter svc, String destName, String messageBody) throws TwitterException {
        String message = messageBody;        
        if (destName != null) {
            message = "@" + destName + " " + messageBody;  
        }
        
        if (log().isDebugEnabled()) {
            log().debug("Updating service: " + svc + " with status: " + message);
        }
        try {
            return svc.updateStatus(message);
        } catch (TwitterException e) {
            log().error("Failed to update status for user '" + svc.getUserId() + "' at service URL '" + svc.getBaseURL() + "', caught exception: " + e.getMessage());
            throw e;
        }
    }
    
    private DirectMessage directMessage(Twitter svc, String destName, String messageBody) throws TwitterException {
        if (log().isDebugEnabled()) {
            log().debug("Sending direct message to user '" + destName + "' with message '" + messageBody + "'");
        }
        try {
            return svc.sendDirectMessage(destName, messageBody);
        } catch (TwitterException e) {
            log().error("Failed to send DM to user '" + destName + "' from user '"+ svc.getUserId() + "' at service URL '" + svc.getBaseURL() + "', caught exception: " + e.getMessage());
            throw e;
        }
    }
    
    private Twitter buildUblogService(List<Argument> arguments) {
        String serviceUrl = "";
        String authenUser = "";
        String authenPass = "";
        
        for (Argument arg : arguments) {
            if (SERVICE_URL.equals(arg.getSwitch())) {
                serviceUrl = arg.getValue();
            } else if (AUTHEN_USERNAME.equals(arg.getSwitch())) {
                authenUser = arg.getValue().toLowerCase();
            } else if (AUTHEN_PASSWORD.equals(arg.getSwitch())) {
                authenPass = arg.getValue();
            }
        }
        
        if ("".equals(authenUser))
            log().warn("Working with a blank username, perhaps you forgot to set the '" + AUTHEN_USERNAME + "' argument?");
        if ("".equals("authenPassword"))
            log().warn("Working with a blank password, perhaps you forgot to set the '" + AUTHEN_PASSWORD + "' argument?");
        if ("".equals(serviceUrl))
            throw new IllegalArgumentException("Cannot use a blank microblog service URL, did you set the '" + SERVICE_URL + "' argument?");
        
        Twitter svc = new Twitter();
        svc.setBaseURL(serviceUrl);
        svc.setSource("OpenNMS");
        svc.setUserId(authenUser);
        svc.setPassword(authenPass);
        return svc;
    }

    private String buildMessageBody(List<Argument> arguments) {
        String messageBody = "";
        
        for (Argument arg : arguments) {
            if (NotificationManager.PARAM_TEXT_MSG.equals(arg.getSwitch())) {
                messageBody = arg.getValue();
            }
        }
        
        if (messageBody == null) {
            // FIXME We should have a better Exception to use here for configuration problems
            throw new IllegalArgumentException("No message specified, but is required");
        }
        
        // Collapse whitespace in final message
        messageBody.replaceAll("\\s+", " ");
        if (log().isDebugEnabled()) {
            log().debug("Final message body after collapsing whitespace is: '" + messageBody + "'");
        }

        return messageBody;
    }

    private Category log() {
        return ThreadCategory.getInstance(getClass());
    }
}
