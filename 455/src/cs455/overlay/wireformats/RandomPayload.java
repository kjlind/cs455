package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cs455.overlay.util.NodeInfo;

/**
 * RandomPayload is the message type sent during rounds between messaging nodes
 * after they receive the task initiate message. A random payload message has
 * two fields: an integer payload, and a routing plan consisting of an array of
 * NodeInfo objects.
 * 
 * @author Kira Lindburg
 * @date Feb 11, 2014
 */
public class RandomPayload implements Message {
    private static final int TYPE = Protocol.RANDOM_PAYLOAD;

    private int payload;
    private NodeInfo[] routingPlan;

    /**
     * Creates a new RandomPayload message with the provided payload and routing
     * plan.
     */
    public RandomPayload(int payload, NodeInfo[] routingPlan) {
        this.payload = payload;
        this.routingPlan = routingPlan;
    }

    /**
     * Creates a new RandomPayload by unmarshalling the given byte array into
     * the proper fields: type, payload, and routing plan. This constructor
     * should be used on the receiving end of a message in order to unmarshall
     * the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * random payload message; presumably this was created by calling the
     * getBytes() method of a random payload message earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public RandomPayload(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();
        
        // payload field
        payload = din.readInt();

        // routing plan field(s)
        int numberOfNodes = din.readInt();
        routingPlan = new NodeInfo[numberOfNodes];
        for (int i = 0; i < numberOfNodes; ++i) {
            int nodeLength = din.readInt();
            byte[] nodeBytes = new byte[nodeLength];
            din.readFully(nodeBytes);
            routingPlan[i] = new NodeInfo(nodeBytes);
        }
    }

    public int getPayload() {
        return payload;
    }

    public NodeInfo[] getRoutingPlan() {
        return routingPlan;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
            baos));

        // type field
        dout.writeInt(TYPE);
        
        // payload field
        dout.writeInt(payload);

        // routing plan field(s)
        dout.writeInt(routingPlan.length);
        for (NodeInfo node : routingPlan) {
            byte[] nodeBytes = node.getBytes();
            int nodeLength = nodeBytes.length;
            dout.writeInt(nodeLength);
            dout.write(nodeBytes);
        }

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }
    
    @Override
    public String toString(){
        String string = "Random Payload\n";
        string += "payload: " + payload + "\n";
        string += "routing plan: " + Arrays.toString(routingPlan);
        return string;
    }
}
