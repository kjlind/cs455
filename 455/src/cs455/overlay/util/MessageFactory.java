package cs455.overlay.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.PullTrafficSummary;
import cs455.overlay.wireformats.RandomPayload;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;
import cs455.overlay.wireformats.TaskComplete;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TrafficSummary;

/**
 * MessageFactory consists of a static method to create a new Message instance
 * of an appropriate type, based upon the contents of a byte array. This factory
 * should primarily be used for unmarshalling bytes which have just been read
 * from a stream.
 * 
 * @author Kira Lindburg
 * @date Jan 27, 2014
 */
public class MessageFactory {
    /**
     * Creates a new Message of the appropriate type given the contents of the
     * provided byte array. Assumes the first bytes in the array indicate the
     * type of message, as according to the fields defined in Protocol.
     * 
     * @param marshalledBytes bytes containing the data of a message
     * @return a Message of the appropriate type according to the type
     * identifier at the beginning of the marshalled bytes
     * @throws IOException if an I/O error occurs while trying to unmarshal the
     * bytes
     */
    public static Message createMessage(byte[] marshalledBytes)
        throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        int messageType = din.readInt();
        switch (messageType) {
        case Protocol.REGISTER_REQUEST:
            return new RegisterRequest(marshalledBytes);
        case Protocol.REGISTER_RESPONSE:
            return new RegisterResponse(marshalledBytes);
        case Protocol.DEREGISTER_REQUEST:
            return new DeregisterRequest(marshalledBytes);
        case Protocol.DEREGISTER_RESPONSE:
            return new DeregisterResponse(marshalledBytes);
        case Protocol.MESSAGING_NODES_LIST:
            return new MessagingNodesList(marshalledBytes);
        case Protocol.LINK_WEIGHTS:
            return new LinkWeights(marshalledBytes);
        case Protocol.TASK_INITIATE:
            return new TaskInitiate();
        case Protocol.RANDOM_PAYLOAD:
            return new RandomPayload(marshalledBytes);
        case Protocol.TASK_COMPLETE:
            return new TaskComplete(marshalledBytes);
        case Protocol.PULL_TRAFFIC_SUMMARY:
            return new PullTrafficSummary();
        case Protocol.TRAFFIC_SUMMARY:
            return new TrafficSummary(marshalledBytes);
        case Protocol.CONNECTION_INFORMATION:
            return new ConnectionInformation(marshalledBytes);
        default:
            throw new IOException("Unrecognized message type");
        }
    }
}
