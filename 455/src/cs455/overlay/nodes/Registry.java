package cs455.overlay.nodes;

import java.io.IOException;
import java.util.Scanner;

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
        /* parse command line args */
        if (args.length != 1) {
            System.out.println("Usage: Registry portnum");
            System.exit(-1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Portnum must be an integer; " + args[0]
                + " is not an int!");
        }

        /* construct Registry */
        Registry registry = new Registry();

        /* start server thread */
        try {
            registry.startServer(port);
        } catch (IOException e) {
            System.out.println("Unable to set up ServerThread to listen for"
                + " connections; an I/O error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* handle CLI input */
        Scanner kbd = new Scanner(System.in);
        System.out.println("Waiting for a command: ");
        String command = kbd.next();
        while (!command.equals("exit")) {
            // do something here
            System.out.println("You said: " + command); // purely a placeholder
            command = kbd.next();
        }

        /* clean up */
        kbd.close();
        // TODO: nicer exit?
        System.exit(0);
    }

    public Registry() {
        super();
    }

    @Override
    public void handleMessage(byte[] messageBytes) throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
        case Protocol.REGISTER_REQUEST:
            System.out.println("It's a register request!!1!!");
            System.out.println(message);
            break;
        default:
            // TODO: better error handling here
            throw new IOException("Bad message type!");
        }
    }
}
