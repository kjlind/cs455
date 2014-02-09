package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import cs455.overlay.util.LinkInfo;
import cs455.overlay.util.NodeInfo;

/**
 * Dijkstra is an interface between the pre-existing classes implementing
 * Dijkstra's algorithm and the new classes of cs455 (specifically
 * MessagingNode). It converts back and forth between link info as presented
 * within the link weights message from MessagingNode and the graph format
 * ShortestPath wants to work with.
 * 
 * @author Kira Lindburg
 * @date Feb 9, 2014
 */
public class Dijkstra {
    private NodeInfo sourceNode;

    private Graph overlay;
    private ShortestPath pathCalculator;

    /**
     * Creates a Dijkstra object which will calculate shortest distances between
     * nodes in a graph built from the provided link information.
     */
    public Dijkstra(LinkInfo[] linkInformation) {
        /* build a graph from the given links */
        List<Edge> links = new ArrayList<Edge>();
        List<Vertex> nodes = new ArrayList<Vertex>();

        for (LinkInfo nextLink : linkInformation) {
            String nodeAStr = nextLink.getNodeA().toString();
            Vertex nodeA = new Vertex(nodeAStr);
            if (!nodes.contains(nodeA)) {
                nodes.add(nodeA);
            }

            String nodeBStr = nextLink.getNodeB().toString();
            Vertex nodeB = new Vertex(nodeBStr);
            if (!nodes.contains(nodeB)) {
                nodes.add(nodeB);
            }

            links.add(new Edge(nodeAStr, nodeBStr, nextLink.getLinkWeight()));
        }

        overlay = new Graph(nodes, links);

        /* pass the graph to a new instance of shortest path */
        pathCalculator = new ShortestPath(overlay);
    }

    /**
     * Sets the source node for this dijkstra object to the specified node; all
     * future shortest paths calculated will begin from this source.
     */
    public void setSourceNode(NodeInfo sourceNode) {
        this.sourceNode = sourceNode;
        Vertex sourceVertex = new Vertex(sourceNode.toString());
        pathCalculator.execute(sourceVertex);
    }

    /**
     * Retrieves and returns the shortest path from the source node to the
     * specified destination node.
     */
    public List<NodeInfo> getPathTo(NodeInfo destinationNode) {
        Vertex destinationVertex = new Vertex(destinationNode.toString());

        List<Vertex> pathAsVertices = pathCalculator.getPath(destinationVertex);
        List<NodeInfo> pathAsNodes = new ArrayList<NodeInfo>();

        for (Vertex vertex : pathAsVertices) {
            String nodeName = vertex.getCity();

            /* split the name into IPAddress and server port fields */
            Scanner nameScanner = new Scanner(nodeName);
            nameScanner.useDelimiter(":");
            String IPAddress = nameScanner.next();
            int serverPort = nameScanner.nextInt();
            nameScanner.close();

            pathAsNodes.add(new NodeInfo(IPAddress, serverPort));
        }

        return pathAsNodes;
    }

    /**
     * @return a string representing the shortest path from the source node to
     * the specified destination node; the string will contain each node in the
     * path as well as the weights of the links between the nodes
     */
    public String getPathStringTo(NodeInfo destinationNode) {
        if (destinationNode.equals(sourceNode)) {
            return sourceNode.toString();
        }

        String pathStr = "";

        Vertex destinationVertex = new Vertex(destinationNode.toString());
        List<Vertex> pathAsVertices = pathCalculator.getPath(destinationVertex);
        Iterator<Vertex> pathIter = pathAsVertices.iterator();

        Vertex previousNode = pathIter.next();
        String prevNodeName = previousNode.getCity();
        pathStr += prevNodeName + " ";

        while (pathIter.hasNext()) {
            Vertex nextNode = pathIter.next();
            String nextNodeName = nextNode.getCity();

            Edge link = overlay.getEdge(prevNodeName, nextNodeName);
            int linkWeight = link.getDistance();

            pathStr += linkWeight + " ";
            pathStr += nextNodeName + " ";

            previousNode = nextNode;
            prevNodeName = nextNodeName;
        }

        return pathStr;
    }

    /**
     * @return a string which contains the shortest path from the source to
     * every other node in the overlay, each on its own line; each path will
     * contain the nodes along the way as well as the link weights between them
     */
    public String getAllPathStrings() {
        String allPaths = "";

        Iterator<Vertex> vertexIter = overlay.getVertices().iterator();
        while (vertexIter.hasNext()) {
            Vertex vertex = vertexIter.next();
            String nodeName = vertex.getCity();

            /* no need to include shortest path from here to here */
            if (!nodeName.equals(sourceNode.toString())) {
                /* split the name into IPAddress and server port fields */
                Scanner nameScanner = new Scanner(nodeName);
                nameScanner.useDelimiter(":");
                String IPAddress = nameScanner.next();
                int serverPort = nameScanner.nextInt();
                nameScanner.close();

                NodeInfo node = new NodeInfo(IPAddress, serverPort);
                allPaths += getPathStringTo(node);

                /* this is why I used an iterator instead of a for loop above */
                if (vertexIter.hasNext()) {
                    allPaths += "\n";
                }
                // TODO: fix extra blank line output when source node is in
                // middle of list
            }
        }

        return allPaths;
    }
}
