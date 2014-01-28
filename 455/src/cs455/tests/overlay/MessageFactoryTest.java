package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessageFactory;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;

public class MessageFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateRegisterRequest() {
        // valid -- register request
        String IPAddress = "denver.cs.colostate.edu";
        int port = 5555;
        String assignedID = "donuts";
        try {
            byte[] marshalledBytes = new RegisterRequest(IPAddress, port,
                assignedID).getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(message.getType(), Protocol.REGISTER_REQUEST);
            RegisterRequest request = (RegisterRequest) message;
            assertEquals(request.getIPAddress(), IPAddress);
            assertEquals(request.getPort(), port);
            assertEquals(request.getAssignedID(), assignedID);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error :( (see console output for details)");
        }
    }

    @Test
    public void testCreateInvalidType() {
        // invalid -- unrecognized type
        int badType = -1;
        byte[] badBytes = ByteBuffer.allocate(4).putInt(badType).array();
        try {
            Message message = MessageFactory.createMessage(badBytes);
            fail("No IOException thrown with an invalid message type identifier");
            // this is purely so Eclipse quits whining about nothing being done
            // with the message
            System.out.println(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
