package cs455.overlay.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * NodeInfo stores the host name and port on which the server is listening for
 * connections of a registered messaging node.
 * 
 * @author Kira Lindburg
 * @date Feb 5, 2014
 */
public class NodeInfo {
    private String hostName;
    private int serverPort;

    public NodeInfo(String hostName, int serverPort) {
        this.hostName = hostName;
        this.serverPort = serverPort;
    }

    /**
     * Creates a new node info object by interpreting the provided byte array as
     * the needed fields; hostname and server port.
     * 
     * @throws IOException if an I/O error occurs when trying to decode the
     * bytes
     */
    public NodeInfo(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // host name field
        int hostnameLength = din.readInt();
        byte[] hostnameBytes = new byte[hostnameLength];
        din.readFully(hostnameBytes);
        hostName = new String(hostnameBytes);

        // server port field
        serverPort = din.readInt();

        bais.close();
        din.close();
    }

    public String getHostName() {
        return hostName;
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * Encodes this node info object as a sequence of bytes; using the
     * constructor for node info which takes a byte array will decode the
     * resulting bytes.
     * 
     * @return the encoded bytes
     * @throws IOException if an I/O error occurs when trying to encode it as
     * bytes
     */
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
            baos));

        // hostname field
        byte[] hostnameBytes = hostName.getBytes();
        int hostnameLength = hostnameBytes.length;
        dout.writeInt(hostnameLength);
        dout.write(hostnameBytes);

        // server port field
        dout.writeInt(serverPort);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    /**
     * @return true if the other object is an instance of NodeInfo, and the host
     * name and server port of other equal the host name and server port,
     * respectively, of this node info object
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NodeInfo)) {
            return false;
        }

        NodeInfo otherInfo = (NodeInfo) other;
        return (this.getHostName().equals(otherInfo.getHostName()))
            && (this.getServerPort() == otherInfo.getServerPort());
    }

    @Override
    public String toString() {
        return hostName + ":" + serverPort;
    }
}
