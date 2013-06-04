package org.opennms.jaxb.pool;

import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PoolTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(PoolTest.class);

    private JaxbEngineProvider provider;

    private CountDownLatch latch = new CountDownLatch(50);


    @org.junit.Test
    public void testNothing(){
        //
    }

      /**

    @Before
    public void setUp() {

        provider = new JaxbEngineProvider("net.latinus.sinardap.types.cne,net.latinus.sinardap.cne.ws.api");
    }

    @Test
    public void testMarshalling() throws JAXBException {

        Cne cne = new Cne();

        StringWriter sw = new StringWriter();

        provider.marshal(cne, sw);
        LOG.debug("sw " + sw.toString());
        assertTrue(sw.toString().contains("http://latinus.net/wsDinardap/types"));
    }

    @Test
    public void testMarshallingCNE() throws JAXBException {

        CNE cne = new CNE();

        StringWriter sw = new StringWriter();

        provider.marshal(cne, sw);
        LOG.info("sw " + sw.toString());
        assertTrue(sw.toString().contains("http://latinus.net/wsDinardap/types"));
    }

    public void testMultipleThreads() throws Exception {
        LOG.info("Starting 500 marshalls.");

        latch = new CountDownLatch(500);
        ExecutorService executor = Executors.newFixedThreadPool(500);
        final List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 500; i++) {
            final int index = i;
            Future out = executor.submit(new Callable<Object>() {
                public Object call() throws Exception {

                    latch.countDown();
                    Random randomGenerator = new Random();
                    //This is just to introduce various delays on replies.
                    int randomTime = randomGenerator.nextInt(100) + 1;
                    try {
                        Thread.sleep(randomTime);
                    } catch (InterruptedException e) {
                        //Right.
                    }

                    Cne cne = new Cne();
                    StringWriter sw = new StringWriter();
                    provider.marshal(cne, sw);
                    LOG.debug("sw " + sw.toString());
                    return sw;
                }
            });
            futures.add(out);
        }


        latch.await(2, TimeUnit.SECONDS);

        for (int i = 0; i < futures.size(); i++) {
            StringWriter out = (StringWriter) futures.get(i).get();
            assertTrue(out.toString().contains("http://latinus.net/wsDinardap/types"));
        }
        LOG.info("All Marshals done");
    }

     */
}
