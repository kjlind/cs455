package cs455.overlay.nodes;

import java.io.IOException;
import java.util.Scanner;

import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessageFactory;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;

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
public class Registry extends Node implements Runnable {
    /**
     * NodeInfo stores the host name and port on which the server is listening
     * for connections of a registered messaging node.
     * 
     * @author Kira Lindburg
     * @date Feb 5, 2014
     */
    private class NodeInfo {
        private String hostName;
        private int serverPort;

        public NodeInfo(String hostName, int serverPort) {
            this.hostName = hostName;
            this.serverPort = serverPort;
        }

        public String getHostName() {
            return hostName;
        }

        public int getServerPort() {
            return serverPort;
        }

        /**
         * @return true if the other object is an instance of NodeInfo, and the
         * host name and server port of other equal the host name and server
         * port, respectively, of this node info object
         */
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof NodeInfo)) {
                return false;
            }

            NodeInfo otherInfo = (NodeInfo) other;
            return (this.getHostName().equals(otherInfo.getHostName()))
                && (this.getServerPort() == otherInfo.getServerPort());
        }
    }

    /**
     * Creates a new Registry which will run its Server on the specified port,
     * (assuming a valid port number).
     * 
     * @param port the port number on which the new registry should listen for
     * connections
     */
    public Registry(int port) {
        super(port);
    }

    @Override
    public void handleMessage(byte[] messageBytes) throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
        case Protocol.REGISTER_REQUEST:
            System.out.println("It's a register request!!1!!");
            System.out.println(message);

            RegisterRequest request = (RegisterRequest) message;
            handleRegisterRequest(request);
            break;
        default:
            // TODO: better error handling here (just ignore unrecognized types
            // maybe?)
            throw new IOException("Bad message type!");
        }
    }

    /**
     * If the messaging node at the IP address and port listed is not already
     * registered, registers the node. Either way, sends a register response to
     * the messaging node indicating either success or failure.
     */
    private void handleRegisterRequest(RegisterRequest request) {
        NodeInfo info = new NodeInfo(request.getIPAddress(), request.getPort());
        
        
    }

    @Override
    public void run() {
        /* start server thread */
        try {
            startServer();
        } catch (IOException e) {
            System.out.println("Unable to set up ServerThread to listen for"
                + " connections; an I/O error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* handle CLI input */
        handleCommandLine();

        /* clean up */
        cleanUp();
        // TODO: nicer exit?
    }

    /**
     * Helper method for run. Reads and handles commands from the command line
     * in a loop, until the exit command is specified. Returns when the exit
     * command is given.
     */
    private void handleCommandLine() {
        Scanner kbd = new Scanner(System.in);
        System.out.println("Waiting for a command: ");
        String command = kbd.next();
        while (!command.equals("exit")) {
            // do something here
            System.out.println("You said: " + command); // purely a placeholder
            command = kbd.next();
        }

        kbd.close();
    }

    /**
     * Handles any clean up necessary when the Registry exits.
     */
    private void cleanUp() {
    }

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
        Registry registry = new Registry(port);

        /* run things */
        registry.run();

        System.exit(0);
    }
}
