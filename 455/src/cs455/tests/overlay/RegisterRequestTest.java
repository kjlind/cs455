package cs455.tests.overlay;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;

public class RegisterRequestTest {
    private RegisterRequest reginald;

    private String IPAddress = "kitaro.cs.colostate.edu";
    private int port = 19;

    // private String assignedID = "KITTEN";

    @Before
    public void setUp() throws Exception {
        reginald = new RegisterRequest(IPAddress, port);
    }

    @Test
    public void testGetType() {
        assertEquals(reginald.getType(), Protocol.REGISTER_REQUEST);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = reginald.getBytes();

            // unmarshal the message
            RegisterRequest regena = new RegisterRequest(marsha);
            assertEquals(regena.getIPAddress(), IPAddress);
            assertEquals(regena.getPort(), port);
            // assertEquals(regena.getAssignedID(), assignedID);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}
