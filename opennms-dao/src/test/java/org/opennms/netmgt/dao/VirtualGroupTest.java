/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opennms.netmgt.dao;

import org.opennms.netmgt.model.Group;
import java.util.List;
import java.util.Set;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;


/**
 *
 * @author daniele
 */
public class VirtualGroupTest  extends AbstractTransactionalDaoTestCase {
    public void testSave() {
      try
      {
        Group node = new Group();
        getGroupDao().save(node);
        getGroupDao().flush();
        System.out.println( "ID " + node.getId() );
        Group node2 = getGroupDao().load( node.getId() );
        assertNotNull( node2 );
      }
      catch( Exception e)
      {
        e.printStackTrace();
        System.out.println( e.getMessage() );
 
      }
    }

    public void testFindGroupByNode() {
      OnmsNode node1 = getNode1();
      Group myGroup = new Group();
      myGroup.addNode( node1 );
      getGroupDao().save( myGroup );
      List<Group> res = getGroupDao().findGroupByNodeId( node1.getId().longValue() );
      assertTrue( "FindGroupByName return a null result", res != null );
      assertTrue( "FindGroupByName return an empty result", res.size() > 0 );
    }

    public void testFindGroupByDescription() {
      OnmsNode node1 = getNode1();
      Group myGroup = new Group();
      myGroup.setDescription( "test description");
      myGroup.addNode( node1 );
      getGroupDao().save( myGroup );
      List<Group> res = getGroupDao().findGroupByDescription( "fake description" );
      assertTrue( "findGroupByDescription return a null result", res != null );
      assertTrue( "findGroupByDescription must be empty with a fake description", res.size() == 0 );
      res = getGroupDao().findGroupByDescription( "test description" );
      assertTrue( "findGroupByDescription return a null result", res != null );
      assertTrue( "findGroupByDescription return an empty result", res.size() > 0 );
    }

   
    public void testFindGroupByService() {
      Set<OnmsIpInterface> interfaces = getNode1().getIpInterfaces();
      assertTrue( interfaces.size() > 0 );
      Set<OnmsMonitoredService> services = interfaces.iterator().next().getMonitoredServices();
      assertTrue( services.size() > 0 );
      Group myGroup = new Group();
      OnmsMonitoredService service =  services.iterator().next();
      myGroup.addService( service );
      getGroupDao().save( myGroup );
      List<Group> res = getGroupDao().findGroupByServiceId( service.getId().longValue() );
      assertTrue( "testFindGroupByService return a null result", res != null );
      assertTrue( "testFindGroupByService return an empty result", res.size() > 0 );

    }

    public void testAddNode() {
      Group myGroup = new Group();
      myGroup.addNode( getNode1() );
      assertTrue( myGroup.getItems().size() == 1 );
      assertTrue( myGroup.getNumberOfGroups() == 0 );
      assertTrue( myGroup.getNumberOfNodes() == 1 );
      assertTrue( myGroup.getNumberOfServices() == 0 );
    }
    
    public void testAddService() {
      Set<OnmsIpInterface> interfaces = getNode1().getIpInterfaces();
      assertTrue( interfaces.size() > 0 );
      Set<OnmsMonitoredService> services = interfaces.iterator().next().getMonitoredServices();
      assertTrue( services.size() > 0 );
      Group myGroup = new Group();
      myGroup.addService( services.iterator().next() );
      assertTrue( myGroup.getItems().size() == 1 );
      assertTrue( myGroup.getNumberOfGroups() == 0 );
      assertTrue( myGroup.getNumberOfNodes() == 0 );
      assertTrue( myGroup.getNumberOfServices() == 1 );
    }

     public void testAddGroup() {
      Group myGroup = new Group();
      Group internalGroup = new Group();
      myGroup.addGroup( internalGroup );
      assertTrue( myGroup.getItems().size() == 1 );
      assertTrue( myGroup.getNumberOfGroups() == 1 );
      assertTrue( myGroup.getNumberOfNodes() == 0 );
      assertTrue( myGroup.getNumberOfServices() == 0 );
     }




    public void testFindAllGroup() {
        Group node = new Group();
        getGroupDao().save(node);
        getGroupDao().flush();
        assertTrue( getGroupDao().getAllGroups().size() == 1 );
        Group node2 = new Group();
        getGroupDao().save(node2);
        getGroupDao().flush();
        assertTrue( getGroupDao().getAllGroups().size() == 2 );
        node.addGroup( new Group() );
        getGroupDao().update(node);
        getGroupDao().flush();
        assertTrue( getGroupDao().getAllGroups().size() == 2 );
    }
}
