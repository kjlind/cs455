package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.Protocol;

public class ConnectionInformationTest {
    private ConnectionInformation conner;

    private String hostname = "kitaro.cs.colostate.edu";
    private int port = 19;
    private int serverPort = 4444;

    @Before
    public void setUp() throws Exception {
        conner = new ConnectionInformation(hostname, port, serverPort);
    }

    @Test
    public void testGetType() {
        assertEquals(conner.getType(), Protocol.CONNECTION_INFORMATION);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = conner.getBytes();

            // unmarshal the message
            ConnectionInformation carrie = new ConnectionInformation(marsha);
            assertEquals(hostname, carrie.getHostname());
            assertEquals(port, carrie.getPort());
            assertEquals(serverPort, carrie.getServerPort());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }

}
