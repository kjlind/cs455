package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * TaskInitiate is sent from the registry to all registered messaging nodes to
 * indicate that they should begin rounds of messages between each other. The
 * only field it has is the type identifier.
 * 
 * @author Kira Lindburg
 * @date Feb 11, 2014
 */
public class TaskInitiate implements Message {
    private static final int TYPE = Protocol.TASK_INITIATE;

    /**
     * Creates a new task initiate message.
     */
    public TaskInitiate() {

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

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }
}
