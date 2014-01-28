package cs455.overlay.nodes;

import java.io.IOException;


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
    public static void main(String args[]) {
        // TODO: everything!
        MessagingNode node = new MessagingNode();
        int port = Integer.parseInt(args[0]);
        try {
            node.startServer(port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(byte[] message) {
        // TODO Auto-generated method stub

    }
}
