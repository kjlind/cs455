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
    public static final int REGISTER_REQUEST = 21;
    public static final int REGISTER_RESPONSE = 31;
    public static final int DEREGISTER_REQUEST = 41;
    public static final int DEREGISTER_RESPONSE = 51;
    public static final int MESSAGING_NODES_LIST = 61;
    public static final int LINK_WEIGHTS = 71;
    public static final int TASK_INITIATE = 81;
    public static final int RANDOM_PAYLOAD = 91;
    public static final int TASK_COMPLETE = 101;
    public static final int PULL_TRAFFIC_SUMMARY = 111;
    public static final int TRAFFIC_SUMMARY = 121;
    public static final int CONNECTION_INFORMATION = 131;
}
