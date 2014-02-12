package cs455.tests.overlay;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.RandomPayload;

public class RandomPayloadTest {
    private RandomPayload pay;

    private int payload;
    private NodeInfo[] route;

    @Before
    public void setUp() throws Exception {
        payload = 129092436;

        route = new NodeInfo[3];
        route[0] = new NodeInfo("denver", 64372);
        route[1] = new NodeInfo("kitaro", 64374);
        route[2] = new NodeInfo("bierstadt", 64342);

        pay = new RandomPayload(payload, route);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = pay.getBytes();

            // unmarshal the message
            RandomPayload randy = new RandomPayload(marsha);
            assertEquals(payload, randy.getPayload());
            assertArrayEquals(route, randy.getRoutingPlan());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}
