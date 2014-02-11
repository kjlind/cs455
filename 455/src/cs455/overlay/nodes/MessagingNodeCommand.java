package cs455.overlay.nodes;

/**
 * MessagingNodeCommand contains static String fields which designate the values
 * of all currently valid commands for a messaging node; here for organization
 * purposes.
 * 
 * @author Kira Lindburg
 * @date Feb 8, 2014
 */
public class MessagingNodeCommand {
    public static final String LIST_PATHS = "print-shortest-path";
    public static final String LIST_PEERS = "list-peers";
    public static final String EXIT = "exit-overlay";
    public static final String HELP = "help";
}
