package cs455.overlay.nodes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cs455.overlay.tcp.Sender;
import cs455.overlay.tcp.ServerThread;

/**
 * Node is an abstraction of a single node in a network. It has a particular
 * port on which it listens for incoming connection requests from other nodes,
 * via an instance of the Server. It includes a method to respond to any
 * messages received by other nodes (presumably passed to it by a Receiver), and
 * a list of Senders, one for each current connection with another node.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public abstract class Node {
    public static final boolean DEBUG = true;

    private int portnum;

    private List<Sender> senders;

    /**
     * Creates a new node with no set port number; when startServer() is called,
     * a port number will be automatically configured.
     */
    public Node() {
        portnum = 0;
        senders = new LinkedList<Sender>();
    }

    /**
     * Creates a new node which is associated with the given port.
     * 
     * @param port the port number on which this node should listen for incoming
     * connections
     */
    public Node(int port) {
        this();
        portnum = port;
    }

    // TODO: change byte[] to Message, have receiver thread use factory?
    /**
     * Interprets and responds to the provided byte message in some way.
     * 
     * @throws IOException if any I/O error occurs while attempting to interpret
     * or handle the message
     */
    public abstract void handleMessage(byte[] messageBytes) throws IOException;

    /**
     * @return the port number on which this registry is listening for
     * connections
     */
    public int getPort() {
        return portnum;
    }

    /**
     * @return this node's list of senders; it will contain one Sender for each
     * current connection with another node
     */
    public List<Sender> getSenders() {
        return senders;
    }

    /**
     * Adds the provided sender to this node's list of senders (current
     * connections with other nodes).
     */
    public void addSender(Sender senderToAdd) {
        senders.add(senderToAdd);
    }

    /**
     * Creates and starts a new ServerThread associated with this node. Sets the
     * port number for this Node to the specified port.
     * 
     * @param port the port on which the server should run
     * @throws IOException if an I/O error occurs when trying to set up the
     * server
     */
    // protected void startServer(int port) throws IOException {
    // if (DEBUG) {
    // System.out.println("Node: just before starting server");
    // }
    // new ServerThread(port, this).start();
    // portnum = port;
    // if (DEBUG) {
    // System.out.println("Node: just after starting server");
    // }
    // }

    /**
     * Creates and starts a new ServerThread associated with this node. If a
     * port number was specified upon creation of this Node, the new
     * ServerThread will run on the specified port. Otherwise, a port number
     * will be automatically configured; getPort() will return this configured
     * port number.
     * 
     * @throws IOException if an I/O error occurs when trying to set up the
     * server
     */
    protected void startServer() throws IOException {
        if (DEBUG) {
            System.out.println("Node: just before starting server");
            System.out.println("Node: current portnum = " + portnum);
        }

        ServerThread server = new ServerThread(portnum, this);
        server.start();

        /* get the new portnum if one was automatically set up */
        portnum = server.getPort();

        if (DEBUG) {
            System.out.println("Node: just after starting server");
            System.out.println("Node: current portnum = " + portnum);
        }
    }
}
