package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A DeregisterResponse is sent from the Registry to a MessagingNode in reply to
 * a DeregisterRequest from that messaging node. A deregister response has two
 * fields: a boolean (sent as a single byte) which indicates whether the
 * deregistration was successful or not, and a String which contains additional
 * information specifying either the number of nodes currently registered (in
 * the case of successful deregistration) or why the deregistration failed (in
 * the case of unsuccessful deregistration).
 * 
 * @author Kira Lindburg
 * @date Feb 8, 2014
 */
public class DeregisterResponse implements Message {
    private static final int TYPE = Protocol.DEREGISTER_RESPONSE;

    private boolean success;
    private String info;

    /**
     * Creates a new DeregisterResponse with the given success and info fields.
     */
    public DeregisterResponse(boolean success, String info) {
        this.success = success;
        this.info = info;
    }

    /**
     * Creates a new DeregisterResponse by unmarshalling the given byte array
     * into the proper fields: type, success, and info. This constructor should
     * be used on the receiving end of a message in order to unmarshall the
     * previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * deregister response; presumably this was created by calling the
     * getBytes() method of a deregister response earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public DeregisterResponse(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();

        // success field
        success = din.readBoolean();

        // info field (length:info)
        int infoBytesLength = din.readInt();
        byte[] infoBytes = new byte[infoBytesLength];
        din.readFully(infoBytes);
        info = new String(infoBytes);

        bais.close();
        din.close();
    }

    public boolean getSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
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

        // success field
        dout.writeBoolean(success);

        // info field (length:info)
        byte[] infoBytes = info.getBytes();
        int infoBytesLength = infoBytes.length;
        dout.writeInt(infoBytesLength);
        dout.write(infoBytes);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Deregister Response\n";
        string += "success: " + success + "\n";
        string += "info: " + info;
        return string;
    }
}
