package cs455.overlay.nodes;

/**
 * RegistryCommand contains only static String fields; these fields specify the
 * values of currently valid command line commands which may be given to the
 * registry. They are assembled together here for convenience and clarity.
 * (Ideally this will help keep me more organized and less insane as I continue
 * hacking this thing together!!! :D)
 * 
 * @author Kira Lindburg
 * @date Feb 8, 2014
 */
public class RegistryCommand {
    public static final String LIST_NODES = "list-messaging-nodes";
    public static final String LIST_WEIGHTS = "list-weights";
    public static final String SETUP_OVERLAY = "setup-overlay";
    public static final String EXIT = "exit";
}
