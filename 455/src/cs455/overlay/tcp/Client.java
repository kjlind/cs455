package cs455.overlay.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.ConnectionInformation;

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
    private static final boolean DEBUG = false;

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
     * IPAddress on the given port number. If successful, creates and starts a
     * ReceiverThread with the newly connected socket, and creates and returns a
     * Sender with the new socket. The name of the new sender will be set to
     * "IPAddress:port". Adds the newly created sender to the targeted node.
     * 
     * @param IPAddress the address of the server to connect to
     * @param port the port number of the server to connect to
     * @return the newly created sender, for convenience
     * @throws IOException if an I/O error occurs while trying to connect
     * @throws UnknownHostException if the host cannot be determined
     */
    public Sender connectTo(String IPAddress, int port)
        throws UnknownHostException, IOException {
        if (DEBUG) {
            System.out.println("Client: Attempting to connect to " + IPAddress
                + " on port " + port);
        }

        Socket socket = new Socket(IPAddress, port);
        if (DEBUG) {
            System.out.println("Client: Successfully connected");
        }

        Sender sender = new Sender(socket);
        new ReceiverThread(socket, targetedNode).start();

        /* set name of sender to hostname and server port of receiving node */
        String senderName = sender.getReceiverHostName() + ":" + port;
        sender.setName(senderName);

        if (DEBUG) {
            System.out.println("Client: Set name of newly created sender to "
                + senderName);
            System.out.println("Client: Sending connection information");
        }

        /* send connection information to the receiving node */
        String localHostAddress = sender.getLocalHostName();
        int localPort = sender.getLocalPort();
        int localServerPort = targetedNode.getPort();
        ConnectionInformation info = new ConnectionInformation(
            localHostAddress, localPort, localServerPort);
        sender.sendBytes(info.getBytes());

        targetedNode.addSender(sender);

        return sender;
    }
}
