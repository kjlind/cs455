package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * TrafficSummary is sent from the messaging nodes to the registry upon request.
 * It includes a summary of all traffic which flowed out of, through, and into
 * this messaging node. It has the following fields:
 * 
 * Message Type
 * 
 * Node IP address
 * 
 * Node Port number
 * 
 * Number of messages sent
 * 
 * Summation of sent messages
 * 
 * Number of messages received
 * 
 * Summation of received messages
 * 
 * Number of messages relayed
 * 
 * @author Kira Lindburg
 * @date Feb 12, 2014
 */
public class TrafficSummary implements Message {
    private static final int TYPE = Protocol.TRAFFIC_SUMMARY;

    private String IPAddress;
    private int port;

    private int sentTracker;
    private long sentSum;
    private int receivedTracker;
    private long receivedSum;
    private int relayedTracker;

    /**
     * Creates a new TaskComplete message with the given IPAddress and port
     * fields.
     */
    public TrafficSummary(String IPAddress, int port, int sentTracker,
        long sentSum, int receivedTracker, long receivedSum, int relayedTracker) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.sentTracker = sentTracker;
        this.sentSum = sentSum;
        this.receivedTracker = receivedTracker;
        this.receivedSum = receivedSum;
        this.relayedTracker = relayedTracker;
    }

    /**
     * Creates a new TrafficSummary message by unmarshalling the given byte
     * array into the proper fields: type, IPAddress, port, sentTracker,
     * sentSum, receivedTracker, receivedSum, and relayedTracker. This
     * constructor should be used on the receiving end of a message in order to
     * unmarshall the previously marshalled bytes.
     * 
     * @param marshalledBytes a byte array containing marshalled bytes for a
     * TrafficSummary message; presumably this was created by calling the
     * getBytes() method of a TrafficSummary message earlier
     * @throws IOException if an I/O error occurs while attempting to unmarshal
     * the bytes
     */
    public TrafficSummary(byte[] marshalledBytes) throws IOException {
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

        // sent counters fields
        sentTracker = din.readInt();
        sentSum = din.readLong();

        // received counters fields
        receivedTracker = din.readInt();
        receivedSum = din.readLong();

        // relayed counter field
        relayedTracker = din.readInt();

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

    public int getSentTracker() {
        return sentTracker;
    }

    public long getSentSum() {
        return sentSum;
    }

    public int getReceivedTracker() {
        return receivedTracker;
    }

    public long getReceivedSum() {
        return receivedSum;
    }

    public int getRelayedTracker() {
        return relayedTracker;
    }

    /**
     * @return Protocol.TRAFFIC_SUMMARY
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

        // sent counters fields
        dout.writeInt(sentTracker);
        dout.writeLong(sentSum);

        // received counters fields
        dout.writeInt(receivedTracker);
        dout.writeLong(receivedSum);

        // relayed counter field
        dout.writeInt(relayedTracker);

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Traffic Summary\n";
        string += "IPAddress: " + IPAddress + "\n";
        string += "port: " + port + "\n";
        string += "sentTracker: " + sentTracker + "\n";
        string += "sentSum: " + sentSum + "\n";
        string += "receivedTracker: " + receivedTracker + "\n";
        string += "receivedSum: " + receivedSum + "\n";
        string += "relayedTracker: " + relayedTracker;
        return string;
    }
}
