package cs455.overlay.nodes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import cs455.overlay.tcp.Client;
import cs455.overlay.tcp.Sender;
import cs455.overlay.wireformats.RegisterRequest;

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
    private static final boolean DEBUG = true;

    // TODO: remove assignedID (and portnum from constructor)
    private final int portnum; // TODO: move this to Node?
    private final String assignedID;
    private final String registryHost;
    private final int registryPort;

    public MessagingNode(int portnum, String assignedID, String registryHost,
        int registryPort) {
        super();
        this.portnum = portnum;
        this.assignedID = assignedID;
        this.registryHost = registryHost;
        this.registryPort = registryPort;
    }

    public int getPort() {
        return portnum;
    }

    public String getAssignedID() {
        return assignedID;
    }

    /**
     * @return the host name of the machine on which this MessagingNode resides
     * @throws UnknownHostException if the DNS lookup of the IP address fails
     */
    public String getLocalIPAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public String getRegistryHost() {
        return registryHost;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    @Override
    public void handleMessage(byte[] message) {
        // TODO Auto-generated method stub

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
        try {
            // TODO: startServer without specifying a portnum
            startServer(portnum);
        } catch (IOException e) {
            System.out.println("Unable to set up ServerThread to listen for"
                + " connections; an I/O error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* connect to Registry */
        Sender registrySender = null;
        try {
            registrySender = connectToRegistry();
        } catch (UnknownHostException e) {
            System.out.println("Unable to connect to registry at the provided"
                + " hostname: " + registryHost + " and port number: "
                + registryPort);
            System.out.println("Unknown host; seems no one is listening"
                + " there!");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Unable to connect to registry at the provided"
                + " hostname: " + registryHost + " and port number: "
                + registryPort);
            System.out.println("An I/O error occured");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* send register request */
        try {
            sendRegisterRequest(registrySender);
        } catch (IOException e) {
            System.out.println("Unable to send the register request; an I/O"
                + " error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            // TODO: disconnect more gracefully
            System.exit(-1);
        }

        /* handle CLI input */
        handleCommandLine();

        /* deregister and clean up */
        deregisterAndCleanUp(registrySender);
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
    private Sender connectToRegistry() throws UnknownHostException, IOException {
        if (DEBUG) {
            System.out.println("Main MN: connecting to registry");
        }

        return new Client(this).connectTo(registryHost, registryPort);
    }

    /**
     * Helper method for run. Attempts to send a register request from this node
     * to the receiver at the other end of the provided sender (assumed to be
     * connected to the registry); throws any unrecoverable errors.
     * 
     * @throws IOException if an I/O error occurs when trying to send the
     * register request
     */
    private void sendRegisterRequest(Sender registrySender) throws IOException {
        RegisterRequest request = new RegisterRequest(getLocalIPAddress(),
            portnum, assignedID);

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
     * Helper method for run. Sends a deregister request from the given node,
     * and handles any clean up needed.
     */
    private void deregisterAndCleanUp(Sender registrySender) {
        // send a deregister request
        // TODO: nicer exiting
        // TODO: how to handle failed deregistration? should probably allow user
        // to try again, and/or re-enter CLI loop
    }

    public static void main(String args[]) {
        // TODO: only read registry host and registry port from command line
        /* parse command line arguments */
        if (args.length != 4) {
            System.out.println("Usage: MessagingNode portnum assignedID"
                + " registryHost registryPort");
            System.exit(-1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("portnum must be an integer; " + args[0]
                + " is not an int!");
            System.exit(-1);
        }

        String assignedID = args[1];
        String registryHost = args[2];

        int registryPort = 0;
        try {
            registryPort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("registryPort must be an integer; " + args[3]
                + " is not an int!");
        }

        /* construct MessagingNode */
        MessagingNode node = new MessagingNode(port, assignedID, registryHost,
            registryPort);

        /* run stuff */
        node.run();

        System.exit(0);
    }
}
