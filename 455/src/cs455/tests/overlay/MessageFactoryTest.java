package cs455.tests.overlay;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.util.MessageFactory;
import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RandomPayload;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;

public class MessageFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateRegisterRequest() {
        // valid -- register request
        String IPAddress = "denver.cs.colostate.edu";
        int port = 5555;
        // String assignedID = "donuts";
        try {
            byte[] marshalledBytes = new RegisterRequest(IPAddress, port)
                .getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(message.getType(), Protocol.REGISTER_REQUEST);
            RegisterRequest request = (RegisterRequest) message;
            assertEquals(request.getIPAddress(), IPAddress);
            assertEquals(request.getPort(), port);
            // assertEquals(request.getAssignedID(), assignedID);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " RegisterRequest :( (see console output for details)");
        }
    }

    @Test
    public void testCreateRegisterResponse() {
        // valid -- register response
        boolean success = true;
        String info = "Yay!";
        try {
            byte[] marshalledBytes = new RegisterResponse(success, info)
                .getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(message.getType(), Protocol.REGISTER_RESPONSE);
            RegisterResponse response = (RegisterResponse) message;
            assertEquals(success, response.getSuccess());
            assertEquals(info, response.getInfo());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " RegisterResponse :( (see console output for details)");
        }
    }

    @Test
    public void testCreateDeregisterRequest() {
        // valid -- deregister request
        String IPAddress = "denver.cs.colostate.edu";
        int port = 5555;
        try {
            byte[] marshalledBytes = new DeregisterRequest(IPAddress, port)
                .getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(message.getType(), Protocol.DEREGISTER_REQUEST);
            DeregisterRequest request = (DeregisterRequest) message;
            assertEquals(request.getIPAddress(), IPAddress);
            assertEquals(request.getPort(), port);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " DeregisterRequest :( (see console output for details)");
        }
    }

    @Test
    public void testCreateMessagingNodesList() {
        // valid
        NodeInfo[] nod = new NodeInfo[3];
        nod[0] = new NodeInfo("denver", 64372);
        nod[1] = new NodeInfo("kitaro", 64374);
        nod[2] = new NodeInfo("bierstadt", 64342);
        try {
            byte[] marshalledBytes = new MessagingNodesList(nod).getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(Protocol.MESSAGING_NODES_LIST, message.getType());
            MessagingNodesList list = (MessagingNodesList) message;
            assertArrayEquals(nod, list.getMessagingNodes());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " MessagingNodesList :( (see console output for details)");
        }
    }
    
    @Test
    public void testCreateRandomPayload(){
        // valid
        int payload = 13242;
        NodeInfo[] route = new NodeInfo[3];
        route[0] = new NodeInfo("denver", 64372);
        route[1] = new NodeInfo("kitaro", 64374);
        route[2] = new NodeInfo("bierstadt", 64342);
        try {
            byte[] marshalledBytes = new RandomPayload(payload, route).getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(Protocol.RANDOM_PAYLOAD, message.getType());
            RandomPayload pay = (RandomPayload) message;
            assertEquals(payload, pay.getPayload());
            assertArrayEquals(route, pay.getRoutingPlan());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " MessagingNodesList :( (see console output for details)");
        }
    }

    @Test
    public void testCreateConnectionInformation() {
        // valid -- connection information
        String IPAddress = "denver.cs.colostate.edu";
        int port = 5555;
        int serverPort = 8574;
        try {
            byte[] marshalledBytes = new ConnectionInformation(IPAddress, port,
                serverPort).getBytes();
            Message message = MessageFactory.createMessage(marshalledBytes);
            assertEquals(message.getType(), Protocol.CONNECTION_INFORMATION);
            ConnectionInformation info = (ConnectionInformation) message;
            assertEquals(info.getHostname(), IPAddress);
            assertEquals(info.getPort(), port);
            assertEquals(info.getServerPort(), serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected I/O error on getBytes() method of valid"
                + " ConnectionInformation :( (see console output for details)");
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
