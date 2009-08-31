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

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


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
    
    public MicroblogNotificationStrategy() {
    }

    public int send(List<Argument> arguments) {
        Twitter svc = buildUblogService(arguments);
        String message = buildMessage(arguments);
        Status status;
        
        if (log().isDebugEnabled()) {
            log().debug("Updating service: " + svc + " with status: " + message);
        }
        try {
            status = svc.updateStatus(message);
        } catch (TwitterException e) {
            log().error("Failed to update status for user '" + svc.getUserId() + "' at service URL '" + svc.getBaseURL() + "', caught exception: " + e.getMessage());
            return 1;
        }
        
        log().info("Successfully updated status: '" + status.getText() + "'");
        
        return 0;
    }
    
    public Twitter buildUblogService(List<Argument> arguments) {
        String serviceUrl = "";
        String authenUser = "";
        String authenPass = "";
        
        for (Argument arg : arguments) {
            if (SERVICE_URL.equals(arg.getSwitch())) {
                serviceUrl = arg.getValue();
            } else if (AUTHEN_USERNAME.equals(arg.getSwitch())) {
                authenUser = arg.getValue();
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

    private String buildMessage(List<Argument> arguments) {
        String message = null;
        
        for (Argument arg : arguments) {
            if (NotificationManager.PARAM_TEXT_MSG.equals(arg.getSwitch())) {
                message = arg.getValue();
            }
        }
        
        if (message == null) {
            // FIXME We should have a better Exception to use here for configuration problems
            throw new IllegalArgumentException("No message specified, but is required");
        }

        return message;
    }

    private Category log() {
        return ThreadCategory.getInstance(getClass());
    }
}
