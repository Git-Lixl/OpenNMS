/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.service.chef;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.SearchResult;
import org.junit.Test;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * This test is just a convenient place for me to poke at
 * various aspects of the jclouds-chef API
 *  
 * @author <A HREF="Jeff Gehlbach">jeffg@opennms.org</A>
 *
 */
public class ChefMonkeyTest {

    @Test
    //@Ignore
    public void testConnectToPlatform() throws IOException, JSONException {
        String cred = Files.toString(new File("/home/jeffg/.chef/jeffg.pem"), Charsets.UTF_8);
        ContextBuilder cb = ContextBuilder.newBuilder("chef").endpoint("https://api.opscode.com/organizations/jeffgtesting").credentials("jeffg", cred);
        ChefContext con = cb.build();
        
        ChefApi api = con.getApi();
        SearchResult<? extends Node> chefNodes = api.searchNodes();
        
        JSONObject radiant = new JSONObject(api.getDatabagItem("apps", "radiant").toString());
        System.out.println("radiant repository from Databag 'apps/radiant' is '" + radiant.getString("repository") + "'");
        for (String dbagName : api.listDatabags()) {
            System.out.println("Databag: " + dbagName);
            for (String dbagItemName : api.listDatabagItems(dbagName)) {
                System.out.println("DatabagItem '" + dbagItemName + "': " + api.getDatabagItem(dbagName, dbagItemName));
            }
        }
        
        for (Node node : chefNodes) {
            if (node.getAutomatic().containsKey("cloud")) {
                System.out.println("CLOUD!!1!  " + node.getAutomatic().get("cloud"));
                JSONObject cloud = new JSONObject(node.getAutomatic().get("cloud").toString());
                System.out.println("\tCloud provider: " + cloud.getString("provider"));
            }
            System.out.println("Node in environment '" + node.getChefEnvironment() + "': " + node.toString());
        }
    }

}
