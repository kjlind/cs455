package cs455.overlay.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cs455.overlay.nodes.Node;

/**
 * A Client forms connections with other nodes; it has some associated 'owner'
 * node and consists of a single method which attempts to connect with a server
 * at a given IP and port. If successful, it creates a ReceiverThread and a
 * Sender associated with the newly created link.
 * 
 * @author Kira Lindburg
 * @date Jan 28, 2014
 */
public class Client {
    private static final boolean DEBUG = true;

    private Node targetedNode;

    /**
     * Creates a new Client associated with the given node; all receivers and
     * senders subsequently created for connections set up by this client will
     * be associated with the node.
     */
    public Client(Node targetedNode) {
        this.targetedNode = targetedNode;
    }

    /**
     * Attempts to set up a new connection with a server listening at the given
     * IPAddress on the given port number.
     * 
     * @param IPAddress the address of the server to connect to
     * @param port the port number of the server to connect to
     * @throws IOException if an I/O error occurs while trying to connect
     * @throws UnknownHostException if the host cannot be determined
     */
    public void connectTo(String IPAddress, int port)
            throws UnknownHostException, IOException {
        if (DEBUG) {
            System.out.println("Client: Attempting to connect to " + IPAddress
                    + " on port " + port);
        }

        Socket socket = new Socket(IPAddress, port);
        new ReceiverThread(socket, targetedNode).start();
        targetedNode.addSender(new Sender(socket));

        if (DEBUG) {
            System.out.println("Client: Successfully connected");
        }
    }
}
