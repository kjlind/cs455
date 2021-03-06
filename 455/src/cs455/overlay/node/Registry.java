package cs455.overlay.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import cs455.overlay.tcp.Sender;
import cs455.overlay.util.LinkInfo;
import cs455.overlay.util.MessageFactory;
import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.PullTrafficSummary;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;
import cs455.overlay.wireformats.TaskComplete;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TrafficSummary;

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
    private static boolean DEBUG = false;
    // private static final int DEFAULT_NUM_CONNECTIONS = 4;

    private List<NodeInfo> registeredNodes;
    private List<NodeInfo> unfinishedNodes;

    private List<TrafficSummary> collectedSummaries;

    private List<LinkInfo> links;

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
        links = new ArrayList<LinkInfo>();
    }

    @Override
    public synchronized void handleMessage(byte[] messageBytes,
        String senderHostName) throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
        case Protocol.REGISTER_REQUEST:
            if (DEBUG) {
                System.out.println("\nRegistry: It's a register request!!1!!");
                System.out.println(message);
            }
            RegisterRequest request = (RegisterRequest) message;
            handleRegisterRequest(request, senderHostName);
            break;
        case Protocol.DEREGISTER_REQUEST:
            if (DEBUG) {
                System.out.println("\nRegistry: Deregister request :( :( :(");
                System.out.println(message);
            }
            DeregisterRequest derequest = (DeregisterRequest) message;
            handleDeregisterRequest(derequest, senderHostName);
            break;
        case Protocol.TASK_COMPLETE:
            if (DEBUG) {
                System.out.println("\nRegistry: got a task complete");
                System.out.println(message);
            }
            TaskComplete complete = (TaskComplete) message;
            handleTaskComplete(complete);
            break;
        case Protocol.TRAFFIC_SUMMARY:
            if (DEBUG) {
                System.out.println("\nRegistry: got a traffic summary");
                System.out.println(message);
            }
            TrafficSummary summary = (TrafficSummary) message;
            handleTrafficSummary(summary);
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
    private void handleRegisterRequest(RegisterRequest request,
        String senderHostName) {
        NodeInfo info = new NodeInfo(request.getIPAddress(), request.getPort());
        System.out.println("Got a register request from " + info + ".");

        boolean success;
        String responseString;
        if (!info.getHostName().equals(senderHostName)) {
            success = false;
            responseString = "Registration failed; the IP in the request did"
                + " not match the IP of the sender";
            /* cheap hack way to make retrieving sender below probably work */
            info = new NodeInfo(senderHostName, request.getPort());
        } else if (!registeredNodes.contains(info)) {
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
    private void handleDeregisterRequest(DeregisterRequest derequest,
        String senderHostName) {
        NodeInfo info = new NodeInfo(derequest.getIPAddress(),
            derequest.getPort());
        System.out.println("Got a deregister request from " + info + ".");

        boolean success;
        String responseString;
        if (!info.getHostName().equals(senderHostName)) {
            success = false;
            responseString = "Registration failed; the IP in the request did"
                + " not match the IP of the sender";
            /* cheap hack way to make retrieving sender below probably work */
            info = new NodeInfo(senderHostName, derequest.getPort());
        } else if (registeredNodes.contains(info)) {
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
     * Removes the node with the ip address and port provided in the task
     * complete message from the list of unfinished nodes. If all nodes have
     * finished, sends a pull traffic summary message.
     */
    private void handleTaskComplete(TaskComplete complete) {
        NodeInfo finishedNode = new NodeInfo(complete.getIPAddress(),
            complete.getPort());
        unfinishedNodes.remove(finishedNode);
        System.out.println("Got a task complete message from " + finishedNode
            + ".");

        if (unfinishedNodes.isEmpty()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            System.out.println("All messaging nodes are finished; pulling"
                + " traffic summaries.");
            // do stuff!!!!
            PullTrafficSummary summa = new PullTrafficSummary();
            for (NodeInfo nextNode : registeredNodes) {
                Sender sender = getSenders().get(nextNode.toString());
                // TODO: handle nullll!
                try {
                    sender.sendBytes(summa.getBytes());
                } catch (IOException e) {
                    System.err.println("Error sending pull traffic summary"
                        + " message to " + nextNode);
                    e.printStackTrace();
                }
            }

            if (DEBUG) {
                System.out.println("All nodes are done! Yeah so close now!");
                System.out.println("sent them all a pull traffic summary");
            }
        }
    }

    /**
     * Stores the traffic summary received; when there are as many as there are
     * registered nodes, prints a summary table.
     */
    private void handleTrafficSummary(TrafficSummary summary) {
        collectedSummaries.add(summary);

        if (collectedSummaries.size() == registeredNodes.size()) {
            printSummaryTable();
        }
    }

    /**
     * Handles formatting and printing of the summary table.
     */
    private void printSummaryTable() {
        System.out.println();
        System.out.format("%-35s %-15s %-15s %-15s %-15s %-15s %n", "node",
            "#sent", "#received", "sum sent", "sum received", "#relayed");
        for (TrafficSummary summary : collectedSummaries) {
            System.out.format("%-35s %-15d %-15d %-15d %-15d %-15d %n",
                summary.getIPAddress() + ":" + summary.getPort(),
                summary.getSentTracker(), summary.getReceivedTracker(),
                summary.getSentSum(), summary.getReceivedSum(),
                summary.getRelayedTracker());
        }
        System.out.println();
        System.out.format("%-35s %-15d %-15d %-15d %-15d %n", "Sum",
            totalSentTrackers(), totalReceivedTrackers(), totalSentSums(),
            totalReceivedSums());
    }

    /**
     * @return the sum of the sent tracker values of summaries collected
     */
    private int totalSentTrackers() {
        int sum = 0;
        for (TrafficSummary summary : collectedSummaries) {
            sum += summary.getSentTracker();
        }
        return sum;
    }

    /**
     * @return the sum of the received tracker values of summaries collected
     */
    private int totalReceivedTrackers() {
        int sum = 0;
        for (TrafficSummary summary : collectedSummaries) {
            sum += summary.getReceivedTracker();
        }
        return sum;
    }

    /**
     * @return the sum of the sent sum values of summaries collected
     */
    private int totalSentSums() {
        int sum = 0;
        for (TrafficSummary summary : collectedSummaries) {
            sum += summary.getSentSum();
        }
        return sum;
    }

    /**
     * @return the sum of the received sum values of summaries collected
     */
    private int totalReceivedSums() {
        int sum = 0;
        for (TrafficSummary summary : collectedSummaries) {
            sum += summary.getReceivedSum();
        }
        return sum;
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
        System.out.println("Starting up server...");
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
        System.out.println("Waiting for a command (enter 'help' for a"
            + " description of commands): ");
        String command = kbd.next();

        while (!command.equals(RegistryCommand.EXIT)) {
            switch (command) {
            case RegistryCommand.HELP:
                helpMessage();
                break;
            case RegistryCommand.LIST_NODES:
                listRegisteredNodes();
                break;
            case RegistryCommand.LIST_WEIGHTS:
                listLinks();
                break;
            case RegistryCommand.SETUP_OVERLAY:
                int numConnections = kbd.nextInt();
                setupOverlay(numConnections);
                break;
            case RegistryCommand.SEND_WEIGHTS:
                sendWeights();
                break;
            case RegistryCommand.START:
                sendTaskInitiate();
                break;
            default:
                System.out.println("Unrecognized command!");
            }
            command = kbd.next();
        }

        kbd.close();
    }

    /**
     * Prints out all currently valid commands.
     */
    private void helpMessage() {
        System.out.println(RegistryCommand.LIST_NODES + ": list info about all"
            + " registered nodes");
        System.out.println(RegistryCommand.LIST_WEIGHTS
            + ": list info about all links currently set up in the overlay");
        System.out.println(RegistryCommand.SETUP_OVERLAY
            + " numConnections: determine links which should be formed and"
            + " send messaging nodes list messages to nodes");
        System.out.println(RegistryCommand.SEND_WEIGHTS
            + ": send a link weights message to all registered nodes");
        System.out.println(RegistryCommand.START
            + ": instruct messaging nodes to begin rounds");
        System.out.println(RegistryCommand.EXIT + ": quit the program");
        System.out.println(RegistryCommand.HELP + ": display this help"
            + " message");
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
     * Prints out info for all current links in the overlay.
     */
    private void listLinks() {
        for (LinkInfo nextLink : links) {
            System.out.println(nextLink);
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
            /* choose the new links */
            NodeInfo nextNode = registeredNodes.get(i);
            NodeInfo[] peers = new NodeInfo[2];

            int peer1Index = (i + 1) % registeredNodes.size();
            int peer2Index = (i + 2) % registeredNodes.size();

            peers[0] = registeredNodes.get(peer1Index);
            peers[1] = registeredNodes.get(peer2Index);

            System.out.println("Instructing " + nextNode + " to connect to "
                + Arrays.toString(peers));

            /* generate weights and add the new links to the list of links */
            Random rand = new Random();
            for (NodeInfo peer : peers) {
                int weight = rand.nextInt(10) + 1;
                links.add(new LinkInfo(nextNode, peer, weight));
            }

            /* send the messaging nodes list to the current node */
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

    /**
     * Attempts to send a link weights message to all currently registered nodes
     * indicating the current set up of links. If any attempt fails, prints an
     * error message and gives up on that attempt.
     */
    private void sendWeights() {
        LinkInfo[] linkArray = new LinkInfo[links.size()];
        for (int i = 0; i < linkArray.length; ++i) {
            linkArray[i] = links.get(i);
        }

        LinkWeights weightsMessage = new LinkWeights(linkArray);
        for (NodeInfo nextNode : registeredNodes) {
            Sender sender = getSenders().get(nextNode.toString());
            // TODO: handle nullll!
            try {
                sender.sendBytes(weightsMessage.getBytes());
            } catch (IOException e) {
                System.out.println("Error sending link weights message to "
                    + nextNode);
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempts to send a task initiate message to all currently registered
     * nodes. If any attempt fails, prints an error message and gives up on that
     * attempt.
     */
    private void sendTaskInitiate() {
        /* initialize list to track who is finished */
        unfinishedNodes = new ArrayList<NodeInfo>();
        unfinishedNodes.addAll(registeredNodes);
        collectedSummaries = new ArrayList<TrafficSummary>();

        /* tell nodes to start sending messages */
        TaskInitiate tanky = new TaskInitiate();
        for (NodeInfo nextNode : registeredNodes) {
            Sender sender = getSenders().get(nextNode.toString());
            // TODO: handle nullll!
            try {
                sender.sendBytes(tanky.getBytes());
            } catch (IOException e) {
                System.out.println("Error sending task initiate message to "
                    + nextNode);
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
            System.err.println("Portnum must be an integer; " + args[0]
                + " is not an int!");
            System.exit(-1);
        }

        /* construct Registry */
        Registry registry = new Registry(port);

        /* run things */
        registry.run();

        registry.cleanUpAndExit();
    }
}
