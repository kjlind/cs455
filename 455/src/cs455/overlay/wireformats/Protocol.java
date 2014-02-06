package cs455.overlay.wireformats;

/**
 * Protocol defines all identifiers for types of messages, as public static
 * fields. These are provided purely for coding clarity and modifiability. All
 * types returned by any classes implementing the message interface should be
 * one of these fields.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class Protocol {
    public static final int REGISTER_REQUEST = 1;
    public static final int REGISTER_RESPONSE = 2;
    public static final int DEREGISTER_REQUEST = 3;
    public static final int DEREGISTER_RESPONSE = 4;
    public static final int MESSAGING_NODES_LIST = 5;
    public static final int LINK_WEIGHTS = 6;
    public static final int TASK_INITIATE = 7;
    public static final int PAYLOAD = 8;
    public static final int TASK_COMPLETE = 9;
    public static final int PULL_TRAFFIC_SUMMARY = 10;
    public static final int TRAFFIC_SUMMARY = 11;
    public static final int CONNECTION_INFORMATION = 12;
}
