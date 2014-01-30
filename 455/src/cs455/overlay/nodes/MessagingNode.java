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
public class MessagingNode extends Node {
    private static final boolean DEBUG = true;

    public static void main(String args[]) {
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
        MessagingNode node = new MessagingNode();

        /* start server thread */
        if (DEBUG) {
            System.out.println("Main MN: setting up server");
        }

        try {
            node.startServer(port);
        } catch (IOException e) {
            System.out.println("Unable to set up ServerThread to listen for"
                + " connections; an I/O error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            System.exit(-1);
        }

        /* connect to Registry */
        if (DEBUG) {
            System.out.println("Main MN: connecting to registry");
        }

        Sender registrySender = null;
        try {
            registrySender = new Client(node).connectTo(registryHost,
                registryPort);
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
        String nodename = "";
        try {
            nodename = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RegisterRequest request = new RegisterRequest(nodename, port,
            assignedID);

        if (DEBUG) {
            System.out.println("Main MN: sending register request to registry");
        }

        try {
            registrySender.sendBytes(request.getBytes());
        } catch (IOException e) {
            System.out.println("Unable to send the register request; an I/O"
                + " error occurred");
            System.out.println("Details:");
            e.printStackTrace();
            // TODO: disconnect more gracefully
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
        // TODO: handle failed deregistration better? should probably allow user
        // to try again, and/or remain in CLI loop

        /* deregister and clean up */
        kbd.close();
        // send a deregister request
        // TODO: nicer exiting
        System.exit(0);
    }

    public MessagingNode() {
        super();
    }

    @Override
    public void handleMessage(byte[] message) {
        // TODO Auto-generated method stub

    }
}
