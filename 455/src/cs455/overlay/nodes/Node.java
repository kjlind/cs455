package cs455.overlay.nodes;

import java.util.LinkedList;
import java.util.List;

import cs455.overlay.tcp.Sender;

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
    private List<Sender> senders;

    public Node() {
        senders = new LinkedList<Sender>();
    }

    // TODO: change byte[] to Message, have receiver thread use factory?
    /**
     * Interprets and responds to the provided byte message in some way.
     */
    public abstract void handleMessage(byte[] message);

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
}