package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A RegisterRequest is a message sent from a MessagingNode to the Registry
 * indicating that the node requests to be added to the registry's network
 * overlay. A register request has four fields: type:int, originating
 * IPAddress:String, originating port:int, and originating node's
 * assignedID:String.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class RegisterRequest implements Message {
    private static final int TYPE = Protocol.REGISTER_REQUEST;

    private String IPAddress;
    private int port;

    // private String assignedID;

    /**
     * Creates a new RegisterRequest with the given IPAddress, port number, and
     * assignedID fields.
     */
    public RegisterRequest(String IPAddress, int port) {
        this.IPAddress = IPAddress;
        this.port = port;
        // this.assignedID = assignedID;
    }

    /**
     * Creates a new RegisterRequest by unmarshalling the given byte array into
     * the proper fields: type, IPAddress, port, and assignedID. This
     * constructor should be used on the receiving end of a message in order to
     * unmarshall the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * register request; presumably this was created by calling the getBytes()
     * method of a register request earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public RegisterRequest(byte[] marshalledBytes) throws IOException {
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

        // assigned ID field (length:assignedID)
        // int IDBytesLength = din.readInt();
        // byte[] assignedIDBytes = new byte[IDBytesLength];
        // din.readFully(assignedIDBytes);
        // assignedID = new String(assignedIDBytes);
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
     * @return the originating assignedID field of this register request
     */
    // public String getAssignedID() {
    // return assignedID;
    // }

    /**
     * @return Protocol.REGISTER_REQUEST
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

        // assigned ID field (length:assignedID)
        // byte[] assignedIDBytes = assignedID.getBytes();
        // int IDBytesLength = assignedIDBytes.length;
        // dout.writeInt(IDBytesLength);
        // dout.write(assignedIDBytes);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Register Request\n";
        string += "IPAddress: " + IPAddress + "\n";
        string += "port: " + port + "\n";
        // string += "assignedID: " + assignedID;
        return string;
    }
}
