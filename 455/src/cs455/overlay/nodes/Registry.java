package cs455.overlay.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cs455.overlay.tcp.Sender;
import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessageFactory;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;

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
    private static boolean DEBUG = true;
    // private static final int DEFAULT_NUM_CONNECTIONS = 4;

    private List<NodeInfo> registeredNodes;

    /**
     * Creates a new Registry which will run its Server on the specified port,
     * (assuming a valid port number).
     * 
     * @param port the port number on which the new registry should listen for
     * connections
     */
    public Registry(int port) {
        super(port);
        registeredNodes = new ArrayList<NodeInfo>();
    }

    @Override
    public synchronized void handleMessage(byte[] messageBytes)
        throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
        case Protocol.REGISTER_REQUEST:
            if (DEBUG) {
                System.out.println("\nRegistry: It's a register request!!1!!");
                System.out.println(message);
            }
            // TODO: verify somewhere that IP in message matches sender...

            RegisterRequest request = (RegisterRequest) message;
            handleRegisterRequest(request);
            break;
        case Protocol.DEREGISTER_REQUEST:
            // TODO: verify somewhere that IP in message matches sender...

            if (DEBUG) {
                System.out.println("\nRegistry: Deregister request :( :( :(");
                System.out.println(message);
            }

            DeregisterRequest derequest = (DeregisterRequest) message;
            handleDeregisterRequest(derequest);

            break;
        case Protocol.CONNECTION_INFORMATION:
            if (DEBUG) {
                System.out.println("\nRegistry: CONNNNNNNECTION info!");
                System.out.println(message);
            }

            ConnectionInformation info = (ConnectionInformation) message;
            handleConnectionInformation(info);
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

        boolean registered = registeredNodes.contains(info);

        boolean success;
        String responseString;
        if (!registered) {
            if (DEBUG) {
                System.out.println("Registry: messaging node at " + info
                    + " was not registered; registering now");
            }

            registeredNodes.add(info);

            success = true;
            responseString = "Registration successful; there are "
                + registeredNodes.size()
                + " MessagingNodes currently registered";
        } else {
            success = false;
            responseString = "Registration failed; a node at " + info
                + " was already registered";
        }

        /* send response message */
        Sender sender = getSenders().get(info.toString());
        // TODO: handle nullness

        RegisterResponse response = new RegisterResponse(success,
            responseString);
        try {
            sender.sendBytes(response.getBytes());
        } catch (IOException e) {
            System.out.println("Unable to send response to node at " + info
                + "; removing from registry");
            e.printStackTrace();
            registeredNodes.remove(info);
        }
    }

    /**
     * If the messaging node at the IP address and port listed is currently
     * registered, deregisters the node. Either way, sends a deregister response
     * to the messaging node indicating either success or failure.
     */
    private void handleDeregisterRequest(DeregisterRequest derequest) {
        NodeInfo info = new NodeInfo(derequest.getIPAddress(),
            derequest.getPort());

        boolean registered = registeredNodes.contains(info);

        boolean success;
        String responseString;
        if (registered) {
            if (DEBUG) {
                System.out.println("Registry: messaging node at " + info
                    + " was registered; deregistering now");
            }

            registeredNodes.remove(info);

            success = true;
            responseString = "Deregistration successful; there are now "
                + registeredNodes.size()
                + " MessagingNodes currently registered";
        } else {
            success = false;
            responseString = "Deregistration failed; a node at " + info
                + " was not registered";
        }

        /* send response message */
        Sender sender = getSenders().get(info.toString());
        // TODO: handle nullness

        DeregisterResponse response = new DeregisterResponse(success,
            responseString);
        try {
            sender.sendBytes(response.getBytes());
        } catch (IOException e) {
            System.out.println("Unable to send response to node at " + info
                + "; removing from registry");
            e.printStackTrace();
            registeredNodes.remove(info);
        }
    }

    /**
     * If a sender is stored using the key hostname:port (using the values from
     * info for hostname and port), retrieves and removes the sender, updates
     * the key to be hostname:serverPort, and then stores this back to the
     * table. This update is required so that the registry can later properly
     * identify which sender it should use to send responses to whichever node
     * initiated a connection and sent this message.
     */
    private void handleConnectionInformation(ConnectionInformation info) {
        String nameToLookFor = info.getHostname() + ":" + info.getPort();
        Sender senderToUpdate = getSenders().get(nameToLookFor);

        if (DEBUG) {
            System.out.println("Registry: Looking for " + nameToLookFor);
        }
        if (senderToUpdate != null) {
            getSenders().remove(nameToLookFor);
            String updatedName = info.getHostname() + ":"
                + info.getServerPort();
            senderToUpdate.setName(updatedName);
            getSenders().put(updatedName, senderToUpdate);

            if (DEBUG) {
                System.out.println("Registry: found sender with name "
                    + nameToLookFor + "; updated to name " + updatedName);
            }
        }
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
        cleanUpAndExit();
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

        while (!command.equals(RegistryCommand.EXIT)) {
            switch (command) {
            case RegistryCommand.LIST_NODES:
                listRegisteredNodes();
                break;
            case RegistryCommand.SETUP_OVERLAY:
                int numConnections = kbd.nextInt();
                setupOverlay(numConnections);
                break;
            default:
                System.out.println("Unrecognized command!");
            }
            command = kbd.next();
        }

        kbd.close();
    }

    /**
     * Helper method for handleCommandLine(); prints out info for all registered
     * nodes.
     */
    private void listRegisteredNodes() {
        for (NodeInfo nextNode : registeredNodes) {
            System.out.println(nextNode);
        }
    }

    /**
     * Helper method for handleCommandLine(); constructs a messaging overlay and
     * sends the appropriate messages to the nodes.
     */
    private void setupOverlay(int numConnections) {
        // TODO: ignoring the number of connections for now and assuming four
        // (and ten nodes) for the purposes of HW1
        for (int i = 0; i < registeredNodes.size(); ++i) {
            NodeInfo nextNode = registeredNodes.get(i);
            NodeInfo[] peers = new NodeInfo[2];

            int peer1Index = (i + 1) % registeredNodes.size();
            int peer2Index = (i + 2) % registeredNodes.size();

            peers[0] = registeredNodes.get(peer1Index);
            peers[1] = registeredNodes.get(peer2Index);

            if (DEBUG) {
                System.out.println("Registry: instructing " + nextNode
                    + " to connect to " + Arrays.toString(peers));
            }

            MessagingNodesList list = new MessagingNodesList(peers);

            Sender sender = getSenders().get(nextNode.toString());
            // TODO: handle null probably?

            try {
                sender.sendBytes(list.getBytes());
            } catch (IOException e) {
                // TODO better handling here? try again? ...
                e.printStackTrace();
            }
        }
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
