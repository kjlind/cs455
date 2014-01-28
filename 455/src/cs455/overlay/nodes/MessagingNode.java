package cs455.overlay.nodes;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
        // TODO: better error handling
        MessagingNode node = new MessagingNode();

        // set up server
        if (DEBUG) {
            System.out.println("Main MN: setting up server");
        }

        int port = Integer.parseInt(args[0]);
        try {
            node.startServer(port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // connect to registry
        if (DEBUG) {
            System.out.println("Main MN: connecting to registry");
        }

        String assignedID = args[1];
        String registryHost = args[2];
        int registryPort = Integer.parseInt(args[3]);
        try {
            new Client(node).connectTo(registryHost, registryPort);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // token message sending to test; this will need to change a lot
        List<Sender> senders = new ArrayList<Sender>();
        senders.addAll(node.getSenders());

        if (DEBUG) {
            System.out.println("Main MN: sending message to registry");
            System.out.println("Main MN: #senders = " + senders.size());
        }

        Sender registrySender = senders.get(0);
        RegisterRequest request = new RegisterRequest("shrantiquid", port,
            assignedID);
        try {
            registrySender.sendBytes(request.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MessagingNode() {
        super();
    }

    @Override
    public void handleMessage(byte[] message) {
        // TODO Auto-generated method stub

    }
}
