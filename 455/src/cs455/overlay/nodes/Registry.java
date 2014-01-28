package cs455.overlay.nodes;

import java.io.IOException;

import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessageFactory;
import cs455.overlay.wireformats.Protocol;

/**
 * The Registry coordinates a network of MessagingNodes. It maintains a record
 * of currently registered nodes; functionality is provided to register and
 * de-register a messaging node. All registered nodes will be used to construct
 * a network overlay in which each node has exactly N (specified as a command
 * line argument) connections with other nodes, chosen mostly at random but
 * ensuring that the overlay formed is a connected graph. Once the overlay is
 * formed, nodes will be sent a message detailing which other nodes with which
 * they should form a connection. Additionally, the registry assigns a random
 * weight from 1-10 to each link and informs all messaging nodes. Finally, the
 * registry will instruct all nodes to begin sending rounds of packets through
 * the network, and collect and display activity summaries from all nodes in the
 * network. A command line interface is provided for listing details about
 * registered nodes and links, setting up an overlay, computing link weights,
 * and instructing messaging nodes to begin sending packets.
 * 
 * @author Kira Lindburg
 * @date Jan 22, 2014
 */
public class Registry extends Node {
    public static void main(String args[]) {
        Registry registry = new Registry();
        // TODO: more stringent error checking/ handling
        int port = Integer.parseInt(args[0]);
        try {
            registry.startServer(port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Registry(){
        super();
    }

    @Override
    public void handleMessage(byte[] messageBytes) throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
            case Protocol.REGISTER_REQUEST:
                System.out.println("It's a register request!!1!!");
                break;
            default:
                // TODO: better error handling here
                throw new IOException("Bad message type!");
        }
    }
}
