package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.Protocol;

public class DeregisterRequestTest {
    private DeregisterRequest derek;

    private String IPAddress = "kitaro.cs.colostate.edu";
    private int port = 19;

    @Before
    public void setUp() throws Exception {
        derek = new DeregisterRequest(IPAddress, port);
    }

    @Test
    public void testGetType() {
        assertEquals(derek.getType(), Protocol.DEREGISTER_REQUEST);
    }
    
    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = derek.getBytes();

            // unmarshal the message
            DeregisterRequest derby = new DeregisterRequest(marsha);
            assertEquals(derby.getIPAddress(), IPAddress);
            assertEquals(derby.getPort(), port);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}
