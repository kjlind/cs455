package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A DeregisterRequest is a message sent from a MessagingNode to the Registry
 * indicating that the node requests to be removed from the registry's network
 * overlay. A deregister request has three fields: type:int, originating
 * IPAddress:String, and originating port:int.
 * 
 * @author Kira Lindburg
 * @date Jan 30, 2014
 */
public class DeregisterRequest implements Message {
    private static final int TYPE = Protocol.DEREGISTER_REQUEST;

    private String IPAddress;
    private int port;

    /**
     * Creates a new DeregisterRequest with the given IPAddress and port fields.
     */
    public DeregisterRequest(String IPAddress, int port) {
        this.IPAddress = IPAddress;
        this.port = port;
    }

    /**
     * Creates a new DeregisterRequest by unmarshalling the given byte array
     * into the proper fields: type, IPAddress, and port. This constructor
     * should be used on the receiving end of a message in order to unmarshall
     * the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * deregister request; presumably this was created by calling the getBytes()
     * method of a deregister request earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public DeregisterRequest(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();

        // IP address field (length:IPAddress)
        int IPBytesLength = din.readInt();
        byte[] IPAddressBytes = new byte[IPBytesLength];
        din.readFully(IPAddressBytes);
        IPAddress = new String(IPAddressBytes);

        // port field
        port = din.readInt();

        bais.close();
        din.close();
    }

    /**
     * @return the originating IPAddress field of this register request
     */
    public String getIPAddress() {
        return IPAddress;
    }

    /**
     * @return the originating port field of this register request
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Protocol.DEREGISTER_REQUEST
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

        // IP address field (length:IPAddress)
        byte[] IPAddressBytes = IPAddress.getBytes();
        int IPBytesLength = IPAddressBytes.length;
        dout.writeInt(IPBytesLength);
        dout.write(IPAddressBytes);

        // port field
        dout.writeInt(port);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Deregister Request\n";
        string += "IPAddress: " + IPAddress + "\n";
        string += "port: " + port;
        return string;
    }
}
