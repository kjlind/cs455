package cs455.tests.overlay;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.MessagingNodesList;

public class MessagingNodesListTest {
    private MessagingNodesList mess;

    private NodeInfo[] nod;

    @Before
    public void setUp() throws Exception {
        nod = new NodeInfo[3];
        nod[0] = new NodeInfo("denver", 64372);
        nod[1] = new NodeInfo("kitaro", 64374);
        nod[2] = new NodeInfo("bierstadt", 64342);

        mess = new MessagingNodesList(nod);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = mess.getBytes();

            // unmarshal the message
            MessagingNodesList molly = new MessagingNodesList(marsha);
            assertArrayEquals(nod, molly.getMessagingNodes());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}
