//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.web.map;

/*
 * Created on 6-giu-2007
 *
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.opennms.core.utils.ThreadCategory;

import org.opennms.web.map.MapsConstants;
import org.opennms.web.map.view.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * <p>MapStartUpController class.</p>
 *
 * @author mmigliore
 * @version $Id: $
 * @since 1.8.1
 */
@SuppressWarnings("deprecation")
public class MapStartUpController extends SimpleFormController {
	ThreadCategory log; 

	private Manager manager;
		
	/**
	 * <p>Getter for the field <code>manager</code>.</p>
	 *
	 * @return a {@link org.opennms.web.map.view.Manager} object.
	 */
	public Manager getManager() {
		return manager;
	}

	/**
	 * <p>Setter for the field <code>manager</code>.</p>
	 *
	 * @param manager a {@link org.opennms.web.map.view.Manager} object.
	 */
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
	/** {@inheritDoc} */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response
				.getOutputStream(), "UTF-8"));
		ThreadCategory.setPrefix(MapsConstants.LOG4J_CATEGORY);
		log = ThreadCategory.getInstance(this.getClass());
		
		try{
	        String user = request.getRemoteUser();

	        if (log.isDebugEnabled()) 
	            log.debug("MapStartUp for user:" + user);

			bw.write(ResponseAssembler.getStartupResponse(manager.getProperties(
			                          request.isUserInRole(org.opennms.web.springframework.security.Authentication.ROLE_ADMIN))));
		} catch (Throwable e) {
			log.error("Error in map's startup",e);
			bw.write(ResponseAssembler.getMapErrorResponse(MapsConstants.MAPS_STARTUP_ACTION));
		} finally {
			bw.close();
		}

		return null;
	}
	


}
