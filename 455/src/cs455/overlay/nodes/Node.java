package cs455.overlay.nodes;

/**
 * Node is an abstraction of a single node in a network. It has a particular
 * port on which it listens for incoming connection requests from other nodes,
 * via an instance of the Server. It includes a method to respond to any
 * messages received by other nodes (presumably passed to it by a Receiver).
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public abstract class Node {
    // TODO: change byte[] to Message, have receiver thread use factory?
    public abstract void handleMessage(byte[] message);
}
