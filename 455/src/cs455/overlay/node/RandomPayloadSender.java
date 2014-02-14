package cs455.overlay.node;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import cs455.overlay.dijkstra.Dijkstra;
import cs455.overlay.tcp.Sender;
import cs455.overlay.util.NodeInfo;
import cs455.overlay.wireformats.RandomPayload;

/**
 * RandomPayloadSender handles the sending of random payloads in rounds from a
 * given node; it has an owner node from which the payloads are being sent. For
 * each round, a random node (other than its owner node) will be chosen. The
 * shortest path to that node will then be computed using Dijkstra's algorithm,
 * and five packets containing a payload of a random integer will be sent into
 * the network using the shortest path as a routing plan. The messaging node
 * will complete 5000 such rounds, and then send a TASK_COMPLETE message to the
 * registry.
 * 
 * @author Kira Lindburg
 * @date Feb 11, 2014
 */
public class RandomPayloadSender implements Runnable {
    private static final int NUM_ROUNDS = 5000;
    private static final int NUM_PAYLOADS_PER_ROUND = 5;

    private Hashtable<String, Sender> senders;
    private Dijkstra dijkstra;

    private MessagingNode node;

    public RandomPayloadSender(Hashtable<String, Sender> senders,
        Dijkstra dijkstra, MessagingNode node) {
        this.senders = senders;
        this.dijkstra = dijkstra;
        this.node = node;
    }

    @Override
    public void run() {
        // get list of all nodes from dijkstra
        List<NodeInfo> nodes = dijkstra.getAllNodes();
        nodes.remove(dijkstra.getSourceNode());
        Random rand = new Random();

        // 5000 times:
        for (int i = 0; i < NUM_ROUNDS; ++i) {
            // choose a random node from the list which is not the source node
            int randIndex = rand.nextInt(nodes.size());
            NodeInfo destinationNode = nodes.get(randIndex);
            // retrieve the routing plan for that node
            NodeInfo[] routingPlan = dijkstra.getPathTo(destinationNode);
            // retrieve the sender for next node in the plan
            Sender sender = senders.get(routingPlan[1].toString());
            // 5 times:
            for (int j = 0; j < NUM_PAYLOADS_PER_ROUND; ++j) {
                // get a random integer
                int payload = rand.nextInt();
                // build a random payload message
                RandomPayload message = new RandomPayload(payload, routingPlan);
                // send the message
                try {
                    sender.sendBytes(message.getBytes());
                    node.iSentAPayload(payload);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            /* sleep for some time to avoid overwhelming nodes */
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            node.sendTaskComplete();
            System.out.println("Finished sending all payloads; sent a task"
                + " complete message to the registry.");
        } catch (IOException e) {
            System.err.println("Failed to send task complete! D:");
            e.printStackTrace();
        }
    }

}
