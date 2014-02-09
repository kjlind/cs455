package cs455.overlay.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * LinkInfo stores information about a link between two nodes. It has three
 * fields: a NodeInfo object for each end of the connection, and a weight,
 * indicating the regsistry's assigned weight for that link.
 * 
 * @author Kira Lindburg
 * @date Feb 9, 2014
 */
public class LinkInfo {
    private NodeInfo nodeA;
    private NodeInfo nodeB;
    private int linkWeight;

    /**
     * Creates a new LinkInfo object with the given fields.
     */
    public LinkInfo(NodeInfo nodeA, NodeInfo nodeB, int linkWeight) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.linkWeight = linkWeight;
    }

    /**
     * Creates a new LinkInfo object by interpreting the given bytes as its
     * fields; presumably this is used to decode bytes previously procured from
     * the getBytes() method.
     * 
     * @throws IOException if an I/O error occurs
     */
    public LinkInfo(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // node a field
        int nodeALength = din.readInt();
        byte[] nodeABytes = new byte[nodeALength];
        din.readFully(nodeABytes);
        nodeA = new NodeInfo(nodeABytes);

        // node b field
        int nodeBLength = din.readInt();
        byte[] nodeBBytes = new byte[nodeBLength];
        din.readFully(nodeBBytes);
        nodeB = new NodeInfo(nodeBBytes);

        // weight field
        linkWeight = din.readInt();
    }

    public NodeInfo getNodeA() {
        return nodeA;
    }

    public NodeInfo getNodeB() {
        return nodeB;
    }

    public int getLinkWeight() {
        return linkWeight;
    }

    /**
     * Encodes the fields of this link info object as bytes.
     * 
     * @return the encoded bytes
     * @throws IOException if an I/O error occurs when attempting to encode the
     * bytes
     */
    public byte[] getBytes() throws IOException {
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
            baos));

        // node a field
        byte[] nodeABytes = nodeA.getBytes();
        int nodeALength = nodeABytes.length;
        dout.writeInt(nodeALength);
        dout.write(nodeABytes);

        // node b field
        byte[] nodeBBytes = nodeB.getBytes();
        int nodeBLength = nodeBBytes.length;
        dout.writeInt(nodeBLength);
        dout.write(nodeBBytes);

        // weight field
        dout.writeInt(linkWeight);

        dout.flush();
        bytes = baos.toByteArray();

        baos.close();
        dout.close();

        return bytes;
    }

    @Override
    public String toString() {
        String string = "node A: " + nodeA + " ";
        string += "node B: " + nodeB + " ";
        string += "weight: " + linkWeight;
        return string;
    }
}
