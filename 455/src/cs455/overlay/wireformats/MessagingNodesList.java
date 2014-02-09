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
 * A MessagingNodesList message is sent from the Registry to a MessagingNode to
 * indicate which other messaging nodes it should form connections with.
 * 
 * @author Kira Lindburg
 * @date Feb 8, 2014
 */
public class MessagingNodesList implements Message {
    private static final int TYPE = Protocol.MESSAGING_NODES_LIST;

    private NodeInfo[] messagingNodes;

    /**
     * Creates a new MessageNodesList object which stores the provided list of
     * messagingNodes.
     */
    public MessagingNodesList(NodeInfo[] messagingNodes) {
        this.messagingNodes = messagingNodes;
    }

    /**
     * Creates a new MessagingNodesList by unmarshalling the given byte array
     * into the proper fields: type and an array of NodeInfo objects. This
     * constructor should be used on the receiving end of a message in order to
     * unmarshall the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * messaging nodes list; presumably this was created by calling the
     * getBytes() method of a messaging nodes list earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public MessagingNodesList(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();

        // number of messaging nodes field
        int numberOfNodes = din.readInt();

        // list of messaging nodes field(s)
        messagingNodes = new NodeInfo[numberOfNodes];
        for (int i = 0; i < numberOfNodes; ++i) {
            int infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            messagingNodes[i] = new NodeInfo(infoBytes);
        }
    }

    public NodeInfo[] getMessagingNodes() {
        return messagingNodes;
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

        // number of messaging nodes field
        dout.writeInt(messagingNodes.length);

        // list of messaging nodes field(s)
        for (NodeInfo info : messagingNodes) {
            byte[] infoBytes = info.getBytes();
            int infoLength = infoBytes.length;
            dout.writeInt(infoLength);
            dout.write(infoBytes);
        }

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }
    
    @Override
    public String toString(){
        String string = "Messaging Nodes List\n";
        string += Arrays.toString(messagingNodes);
        return string;
    }
}
