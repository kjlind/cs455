package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * ConnectionInformation is a message sent from a node which has just initiated
 * a connection with another node to that receiving node. It contains the
 * sender's hostname and port for identification of the sender, and more
 * importantly conveys the sender's server port number (the port on which the
 * sender is listening for new connections) to the receiver. The combination of
 * hostname:serverPort is used to uniquely and universally identify all
 * messaging nodes in the network so that routing can be easily accomplished;
 * the receiver of this message should store the contents appropriately.
 * 
 * @author Kira Lindburg
 * @date Feb 5, 2014
 */
public class ConnectionInformation implements Message {
    private static final int TYPE = Protocol.CONNECTION_INFORMATION;

    private String hostname;
    private int port;
    private int serverPort;

    /**
     * Creates a new ConnectionInformation message with the given hostname,
     * port, and serverPort fields.
     */
    public ConnectionInformation(String hostname, int port, int serverPort) {
        this.hostname = hostname;
        this.port = port;
        this.serverPort = serverPort;
    }

    /**
     * Creates a new ConnectionInformation message by unmarshalling the given
     * byte array into the proper fields: type, hostname, port, and serverPort.
     * This constructor should be used on the receiving end of a message in
     * order to unmarshall the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * connection information message; presumably this was created by calling
     * the getBytes() method of a connection information message earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public ConnectionInformation(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();

        // hostname field (length:hostname)
        int hostnameLength = din.readInt();
        byte[] hostnameBytes = new byte[hostnameLength];
        din.readFully(hostnameBytes);
        hostname = new String(hostnameBytes);

        // port field
        port = din.readInt();

        // server port field
        serverPort = din.readInt();
    }

    /**
     * @return the originating host name of this connection information message
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the originating port of this connection information message
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the server port value contained within this connection
     * information message
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * @return Protocol.CONNECTION_INFORMATION
     */
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

        // hostname field (length:hostname)
        byte[] hostnameBytes = hostname.getBytes();
        int hostnameLength = hostnameBytes.length;
        dout.writeInt(hostnameLength);
        dout.write(hostnameBytes);

        // port field
        dout.writeInt(port);

        // server port field
        dout.writeInt(serverPort);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Connection Information\n";
        string += "hostname: " + hostname + "\n";
        string += "port: " + port + "\n";
        string += "serverPort: " + serverPort;
        return string;
    }
}
