package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * PullTrafficSummary is sent from the registry to all registered nodes upon
 * receipt of all TaskComplete messages; it instructs the nodes to send the
 * summary of traffic which passed through.
 * 
 * @author Kira Lindburg
 * @date Feb 12, 2014
 */
public class PullTrafficSummary implements Message {
    private static final int TYPE = Protocol.PULL_TRAFFIC_SUMMARY;

    /**
     * Creates a new pull traffic summary message.
     */
    public PullTrafficSummary() {

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
