package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.util.NodeInfo;

public class NodeInfoTest {

    private NodeInfo izzy;

    private String hostName = "carrot";
    private int serverPort = 8765;

    @Before
    public void setUp() throws Exception {
        izzy = new NodeInfo(hostName, serverPort);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal
            byte[] bytes = izzy.getBytes();

            // unmarshal
            NodeInfo isabella = new NodeInfo(bytes);
            assertEquals(hostName, isabella.getHostName());
            assertEquals(serverPort, isabella.getServerPort());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}
