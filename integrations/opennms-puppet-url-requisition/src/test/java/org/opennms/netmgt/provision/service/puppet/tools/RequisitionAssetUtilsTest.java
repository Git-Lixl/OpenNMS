package org.opennms.netmgt.provision.service.puppet.tools;

import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;

public class RequisitionAssetUtilsTest {
    
    SampleBean sampleBean;
    Map<String, String> properties;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        sampleBean = new SampleBean();
        properties = new HashMap<String, String>();
        properties.put("o", "OpenNMS");
        properties.put("p", "penNMS");
        properties.put("e", "enNMS");
        properties.put("n", "nNMS");
        properties.put("NMS", "NMS");
        sampleBean = (SampleBean) Map2BeanUtils.fill(sampleBean, properties);
        assertEquals("OpenNMS", sampleBean.getO());
        assertEquals("penNMS", sampleBean.getP());
        assertEquals("enNMS", sampleBean.getE());
        assertEquals("nNMS", sampleBean.getN());
        assertEquals("NMS", sampleBean.getNms());
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGenerateRequisitionAssets() throws Exception {
        Collection<RequisitionAsset> requiAssets;
        List<String> requisitionAssetNames = new ArrayList<String>();
        
        requiAssets = RequisitionAssetUtils.generateRequisitionAssets(sampleBean);
        
        assertEquals(5, requiAssets.size());
        
        for (RequisitionAsset requisitionAsset : requiAssets) {
//            System.out.println("requisitionAsset = " + requisitionAsset);
            requisitionAssetNames.add(requisitionAsset.getName());
        }
        
        assertTrue("Is Annotation parameter used?", requisitionAssetNames.contains("OpenNMS"));
    }
}
