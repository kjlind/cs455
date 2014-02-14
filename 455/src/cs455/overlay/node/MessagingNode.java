package cs455.overlay.node;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

import cs455.overlay.dijkstra.Dijkstra;
import cs455.overlay.tcp.Client;
import cs455.overlay.tcp.Sender;
import cs455.overlay.util.MessageFactory;
import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.ConnectionInformation;
import cs455.overlay.wireformats.DeregisterRequest;
import cs455.overlay.wireformats.DeregisterResponse;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RandomPayload;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;
import cs455.overlay.wireformats.TaskComplete;
import cs455.overlay.wireformats.TrafficSummary;

/**
 * When started, a MessagingNode attempts to register with the Registry using
 * the hostname and port provided in the command line arguments. Upon successful
 * registration, the node waits for incoming messages from the registry and
 * responds appropriately. Additionally, the node initiates (upon command from
 * the registry) and accepts (upon detecting an incoming request) connections
 * from other messaging nodes. When the TASK_INITIATE message is received from
 * the registry, the node will begin sending packets in rounds as follows. For
 * each round, a random node (other than itself) will be chosen. The shortest
 * path to that node will then be computed using Dijkstra's algorithm, and five
 * packets containing a payload of a random integer will be sent into the
 * network using the shortest path as a routing plan. The messaging node will
 * complete 5000 such rounds, and then send a TASK_COMPLETE message to the
 * registry. The node will relay/accept incoming packets as required. During the
 * rounds, a number of statistics will be tracked, including the number of
 * packets sent, the sum of the payloads for packets sent, the number of packets
 * received, the sum of the payloads for packets received, and the number of
 * packets relayed. A command line interface is provided for printing the paths
 * calculated to all other nodes in the network, and de-registering from the
 * network; upon successful de-registration, the node will terminate.
 * 
 * @author Kira Lindburg
 * @date Jan 22, 2014
 */
public class MessagingNode extends Node implements Runnable {
    private static final boolean DEBUG = false;

    private final String registryHost;
    private final int registryPort;

    private Sender registrySender;

    private Dijkstra pathCalculator;

    private int sentTracker;
    private long sentSum;
    private int receivedTracker;
    private long receivedSum;
    private int relayedTracker;

    public MessagingNode(String registryHost, int registryPort) {
        super();

        this.registryHost = registryHost;
        this.registryPort = registryPort;
    }

    public String getRegistryHost() {
        return registryHost;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    @Override
    public synchronized void handleMessage(byte[] messageBytes,
        String senderHostName) throws IOException {
        Message message = MessageFactory.createMessage(messageBytes);
        switch (message.getType()) {
        case Protocol.REGISTER_RESPONSE:
            // TODO: what to do with unsuccessful response?
            if (DEBUG) {
                System.out.println("\nMain MN: REGISTER"
                    + " RESPONSESSSSLSLDSJLAKL");
                System.out.println(message);
            }
            RegisterResponse regResponse = (RegisterResponse) message;
            handleRegisterResponse(regResponse);
            break;
        case Protocol.DEREGISTER_RESPONSE:
            if (DEBUG) {
                System.out.println("\nMain MN: DEREGISTER"
                    + " RESPONSESSSSLSLDSJLAKL");
                System.out.println(message);
            }
            DeregisterResponse response = (DeregisterResponse) message;
            handleDeregisterResponse(response);
            break;
        case Protocol.MESSAGING_NODES_LIST:
            if (DEBUG) {
                System.out.println("\nMain MN: messaging nodes list yeyey");
                System.out.println(message);
            }
            MessagingNodesList list = (MessagingNodesList) message;
            handleMessagingNodesList(list);
            break;
        case Protocol.LINK_WEIGHTS:
            if (DEBUG) {
                System.out.println("\nMain MN: link weights yeah!");
                System.out.println(message);
            }

            LinkWeights weights = (LinkWeights) message;
            handleLinkWeights(weights);
            break;
        case Protocol.TASK_INITIATE:
            if (DEBUG) {
                System.out.println("\nMain MN: task initiate, oh yeah");
            }

            handleTaskInitiate();
            break;
        case Protocol.RANDOM_PAYLOAD:
            if (DEBUG) {
                System.out.println("\nMain MN: I gots a payload!!!!!");
                System.out.println(message);
            }

            RandomPayload payload = (RandomPayload) message;
            handleRandomPayload(payload);
            break;
        case Protocol.PULL_TRAFFIC_SUMMARY:
            if (DEBUG) {
                System.out.println("\nMain MN: PTS");
            }
            handlePullTrafficSummary();
            break;
        case Protocol.CONNECTION_INFORMATION:
            if (DEBUG) {
                System.out.println("\nMain MN: CONNNNNNNECTION info!");
                System.out.println(message);
            }

            ConnectionInformation info = (ConnectionInformation) message;
            handleConnectionInformation(info);
            break;
        default:
            System.out.println("Received an unrecognized message type;"
                + " the message contents were " + message);
        }
    }

    /**
     * Prints out the info from the response and gives up and quits if unable to
     * register.
     */
    private void handleRegisterResponse(RegisterResponse response) {
        System.out.println(response.getInfo());
        if (!response.getSuccess()) {
            System.out.println("Unable to register! D: Giving up.");
            cleanUpAndExit();
        }
    }

    /**
     * Closes all senders and exits if deregistration was successful; prints an
     * error message if it was not.
     */
    private void handleDeregisterResponse(DeregisterResponse response) {
        boolean success = response.getSuccess();
        if (success) {
            cleanUpAndExit();
        } else {
            System.out.println(response.getInfo());
        }
    }

    /**
     * Attempts to connect to each node which is listed in the messaging nodes
     * list at corresponding server port provided; if any given connection
     * attempt fails, prints an error message and gives up on that attempt.
     */
    private void handleMessagingNodesList(MessagingNodesList list) {
        System.out.println("Received a messaging nodes list message;"
            + " connecting to peers now.");
        NodeInfo[] peers = list.getMessagingNodes();
        for (NodeInfo nextNode : peers) {
            String IPAddress = nextNode.getHostName();
            int port = nextNode.getServerPort();
            try {
                new Client(this).connectTo(IPAddress, port);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new instance of Dijkstra and passes it the link weights
     * contained in the message; also sets the source to be this node.
     */
    private void handleLinkWeights(LinkWeights weights) {
        System.out.println("Received a link weights message; computing"
            + " shortest paths now.");
        pathCalculator = new Dijkstra(weights.getLinks());

        /* a cheap hack way to get the proper IP for this node */
        String thisIP = registrySender.getLocalHostName();

        NodeInfo thisNode = new NodeInfo(thisIP, getPort());

        if (DEBUG) {
            System.out.println("Main MN: setting source of path calculator to"
                + " this node: " + thisNode);
        }

        pathCalculator.setSourceNode(thisNode);

        if (DEBUG) {
            System.out.println("Main MN: finished setting source of path"
                + " calculator");
        }
    }

    /**
     * Starts the rounds of sending packets.
     */
    private void handleTaskInitiate() {
        System.out.println("Received a task initiate message; beginning"
            + " sending payloads now.");
        RandomPayloadSender snide = new RandomPayloadSender(getSenders(),
            pathCalculator, this);
        new Thread(snide).start();
    }

    /**
     * Updates the sentTracker (increments by one) and the sentSum (adds the
     * value of payload). The name... I don't even know.
     */
    public void iSentAPayload(int payload) {
        sentTracker++;
        sentSum += payload;
    }

    /**
     * Sends a task complete message to the registry.
     * 
     * @throws IOException if I/O error occurs when trying to send the message
     */
    public void sendTaskComplete() throws IOException {
        if (DEBUG) {
            System.out.println("done sending!");
            System.out.println("sentTracker: " + sentTracker);
            System.out.println("sentSum: " + sentSum);
            System.out.println("receivedTracker: " + receivedTracker);
            System.out.println("receivedSum: " + receivedSum);
            System.out.println("relayedTracker: " + relayedTracker);
        }
        TaskComplete imDoneYo = new TaskComplete(
            registrySender.getLocalHostName(), getPort());
        registrySender.sendBytes(imDoneYo.getBytes());
    }

    /**
     * Forwards or receives payloads depending upon the routing plan.
     * 
     * @throws IOException
     */
    private void handleRandomPayload(RandomPayload message) throws IOException {
        NodeInfo[] routingPlan = message.getRoutingPlan();

        // figure out where we are in the routing plan
        int thisIndex = getIndexOfThisNodeIn(routingPlan);
        // if we are the destination:
        if (thisIndex == -1) {
            // TODO: ERRRRRRROORORORORO
            System.err.println("Oh no, I received a packet for which I was not"
                + " in the routing plan :(");
        } else if (thisIndex == routingPlan.length - 1) {
            // add payload to received tracker
            receivedTracker++;
            receivedSum += message.getPayload();

            if (DEBUG) {
                System.out.println("received so far: " + receivedTracker);
            }

        } else {
            // if we're not (ie in the middle somewhere)
            // add to relayed tracker
            relayedTracker++;
            // send the message on to next node in the plan
            NodeInfo nextNode = routingPlan[thisIndex + 1];

            if (DEBUG) {
                System.out.println("Forwarding a packet to: " + nextNode);
                System.out.println("forwarded so far: " + relayedTracker);
            }

            Sender sender = getSenders().get(nextNode.toString());
            // TODO: handle nulllllllll
            sender.sendBytes(message.getBytes());
        }
    }

    /**
     * Helper for handleRandomPayload; finds the occurrence of a node info
     * object corresponding to this node in the routing plan.
     * 
     * @return the index of the first occurrence of a node info corresponding to
     * this node, or -1 if not found
     */
    private int getIndexOfThisNodeIn(NodeInfo[] routingPlan) {
        NodeInfo thisNode = pathCalculator.getSourceNode();

        for (int i = 0; i < routingPlan.length; ++i) {
            if (routingPlan[i].equals(thisNode)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Sends a traffic summary message to the registry and then resets the
     * counters.
     * 
     * @throws IOException if an I/O error occurs
     */
    private void handlePullTrafficSummary() throws IOException {
        TrafficSummary summary = new TrafficSummary(
            registrySender.getLocalHostName(), getPort(), sentTracker, sentSum,
            receivedTracker, receivedSum, relayedTracker);

        registrySender.sendBytes(summary.getBytes());

        resetCounters();
    }

    /**
     * Resets all traffic summary counters to 0 so we're ready for another go.
     */
    private void resetCounters() {
        sentTracker = 0;
        sentSum = 0;
        receivedTracker = 0;
        receivedSum = 0;
        relayedTracker = 0;
    }

    /**
     * If a sender is stored using the key hostname:port (using the values from
     * info for hostname and port), retrieves and removes the sender, updates
     * the key to be hostname:serverPort, and then stores this back to the
     * table. This update is required so that the messaging node can later
     * properly identify which sender it should use to send responses to
     * whichever node initiated a connection and sent this message.
     */
    private void handleConnectionInformation(ConnectionInformation info) {
        String nameToLookFor = info.getHostname() + ":" + info.getPort();
        Sender senderToUpdate = getSenders().get(nameToLookFor);

        if (DEBUG) {
            System.out.println("Main MN: Looking for " + nameToLookFor);
        }
        if (senderToUpdate != null) {
            getSenders().remove(nameToLookFor);
            String updatedName = info.getHostname() + ":"
                + info.getServerPort();
            senderToUpdate.setName(updatedName);
            getSenders().put(updatedName, senderToUpdate);

            if (DEBUG) {
                System.out.println("Main MN: found sender with name "
                    + nameToLookFor + "; updated to name " + updatedName);
            }
        }
    }

    /**
     * Sets up a server thread to listen for incoming connections, connects to
     * and registers with the registry at this messaging node's values for
     * registry host and registry port, then waits for and handles any command
     * line input. Open receiving the exit command, deregisters from the
     * registry and exits.
     */
    @Override
    public void run() {
        /* start server thread */
        System.out.println("Starting up server...");
        try {
            // TODO: startServer without specifying a portnum
            startServer();
        } catch (IOException e) {
            System.out.println("Unable to set up ServerThread to listen for"
                + " connections; an I/O error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* connect to Registry */
        System.out.println("Connecting to registry...");
        try {
            connectToRegistry();
        } catch (UnknownHostException e) {
            System.out.println("Unable to connect to registry at the provided"
                + " hostname: " + registryHost + " and port number: "
                + registryPort);
            System.err.println("Unknown host; seems no one is listening"
                + " there!");
            System.err.println("Details:");
            e.printStackTrace();
            System.err.println("Giving up now.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Unable to connect to registry at the provided"
                + " hostname: " + registryHost + " and port number: "
                + registryPort);
            System.err.println("An I/O error occured");
            System.err.println("Details:");
            e.printStackTrace();
            System.err.println("Giving up now.");
            System.exit(-1);
        }

        /* send register request */
        System.out.println("Sending a register request...");
        try {
            sendRegisterRequest();
        } catch (IOException e) {
            System.err.println("Unable to send the register request; an I/O"
                + " error occurred");
            System.err.println("Details:");
            e.printStackTrace();
            System.err.println("Giving up now.");
            cleanUpAndExit();
        }

        /* handle CLI input */
        handleCommandLine();
    }

    /**
     * Helper method for run. Attempts to connect to the registry using this
     * node's initialized values for registry host and registry port; throws any
     * unrecoverable errors.
     * 
     * @return a Sender which may be used to communicate with the registry
     * @throws IOException if an I/O error occurs when attempting to connect to
     * the registry
     * @throws UnknownHostException if the registry cannot be found at this
     * node's initialized registry name and registry port
     */
    private void connectToRegistry() throws UnknownHostException, IOException {
        if (DEBUG) {
            System.out.println("Main MN: connecting to registry");
        }

        registrySender = new Client(this).connectTo(registryHost, registryPort);
        // getSenders().remove(registrySender);
    }

    /**
     * Helper method for run. Attempts to send a register request from this node
     * to the receiver at the other end of the provided sender (assumed to be
     * connected to the registry); throws any unrecoverable errors.
     * 
     * @throws IOException if an I/O error occurs when trying to send the
     * register request
     */
    private void sendRegisterRequest() throws IOException {
        RegisterRequest request = new RegisterRequest(
            registrySender.getLocalHostName(), getPort());

        if (DEBUG) {
            System.out.println("Main MN: sending register request to registry");
        }

        registrySender.sendBytes(request.getBytes());
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

        while (true) {
            switch (command) {
            case MessagingNodeCommand.HELP:
                helpMessage();
                break;
            case MessagingNodeCommand.LIST_PATHS:
                listPaths();
                break;
            case MessagingNodeCommand.LIST_PEERS:
                listPeers();
                break;
            case MessagingNodeCommand.EXIT:
                try {
                    sendDeregisterRequest();
                } catch (IOException e) {
                    System.out.println("Attempt to send deregistration request"
                        + " failed; an I/O error occurred");
                    System.out.println("Details:");
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Unrecognized command!");
            }
            command = kbd.next();
        }
    }

    /**
     * Prints out all currently valid commands.
     */
    private void helpMessage() {
        System.out.println(MessagingNodeCommand.LIST_PATHS
            + ": print the shortest paths which have been calculated to other"
            + " nodes in the overlay");
        System.out.println(MessagingNodeCommand.LIST_PEERS
            + ": print out node info for all currently maintained connections"
            + " to other nodes");
        System.out.println(MessagingNodeCommand.EXIT
            + ": deregister from the overlay and quit");
        System.out.println(MessagingNodeCommand.HELP
            + ": display this help message");
    }

    /**
     * Lists the shortest paths from here to every other messaging node in the
     * overlay.
     */
    private void listPaths() {
        if (pathCalculator == null) {
            System.out.println("Haven't received a link weights list yet;"
                + " can't do that. (Silly you, did you forget to send the"
                + " command?)");
        } else {
            System.out.println(pathCalculator.getAllPathStrings());
        }
    }

    /**
     * Prints out information about every sender this messaging node is
     * currently maintaining.
     */
    private void listPeers() {
        Enumeration<Sender> senderEnum = getSenders().elements();

        while (senderEnum.hasMoreElements()) {
            Sender nextSender = senderEnum.nextElement();
            if (nextSender.equals(registrySender)) {
                System.out.print("(Registry) ");
            }
            System.out.println(nextSender);
        }
    }

    /**
     * Sends a deregister request from the given node, and handles any clean up
     * needed.
     * 
     * @throws IOException if an I/O error occurs when trying to send the
     * deregister request
     */
    private void sendDeregisterRequest() throws IOException {
        // send a deregister request
        String IPAddress = registrySender.getLocalHostName();
        int port = getPort();
        DeregisterRequest request = new DeregisterRequest(IPAddress, port);
        registrySender.sendBytes(request.getBytes());
    }

    public static void main(String args[]) {
        /* parse command line arguments */
        if (args.length != 2) {
            System.out.println("Usage: MessagingNode registryHost"
                + " registryPort");
            System.exit(-1);
        }

        String registryHost = args[0];

        int registryPort = 0;
        try {
            registryPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("registryPort must be an integer; " + args[3]
                + " is not an int!");
        }

        /* construct MessagingNode */
        MessagingNode node = new MessagingNode(registryHost, registryPort);

        /* run stuff */
        node.run();

        System.exit(0);
    }
}
