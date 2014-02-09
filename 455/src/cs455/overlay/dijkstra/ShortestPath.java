package cs455.overlay.dijkstra;
/**
 * CS 200 Colorado State University, Fall 2012
 *  
 * ShortestPath.java
 * ShortestPath is an implementation of Dijkstra's algorithm.
 * Implementer: Kira Lindburg
 * Date written: 11/27/12
 * Date last updated: 12/5/12
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
//import java.util.Random; //used for unit testing

public class ShortestPath{	
	//defines a 'cityNode', which stores the destination city, the previous city visited, the weight of 
	//the path to this city, and the path to the city
	private class cityNode implements Comparable<cityNode>{
		Vertex vertex; //the destination city
		cityNode previous; //the previous city visited on the way here
		int weight; //the weight of the current path to this city from source
		LinkedList<Vertex> path; //the path from source to this city
		
		public cityNode(Vertex vertex, int weight){
			this.vertex = vertex;
			this.previous = null;
			this.weight = weight;
			this.path = new LinkedList<Vertex>();
		}
		
		public Vertex getVertex(){
			return this.vertex;
		}
		
		public cityNode getPrevious(){
			return this.previous;
		}
		
		public int getWeight(){
			return this.weight;
		}
		
		public LinkedList<Vertex> getPath(){
			return this.path;
		}
		
		public void setPrevious(cityNode newPrev){
			this.previous = newPrev;
		}
		
		public void setWeight(int newWght){
			this.weight = newWght;
		}
		
		public void addToPath(Vertex addCity){
			this.path.addFirst(addCity);
		}
		
		public int compareTo(cityNode other){
			//checks involving infinite weight value (negative value indicates infinity)
			if(this.getWeight() < 0 && other.getWeight() < 0){
				return 0;
			}
			else if(this.getWeight() < 0 && other.getWeight() >= 0){
				return 1;
			}
			else if(this.getWeight() >= 0 && other.getWeight() < 0){
				return -1;
			}
			
			//if both values are finite, simply compare weights
			else if(this.getWeight() == other.getWeight()){
				return 0;
			}
			else if(this.getWeight() > other.getWeight()){
				return 1;
			}
			else{
				return -1;
			}
		}
	}
	
	Graph graph; //the graph for this instance of ShortestPath
	ArrayList<cityNode> paths; //the paths from source (specified in execute) to each of the other cities in graph
	
	//constructor
	public ShortestPath(Graph graph){
		this.graph = graph;
		execute(graph.getVertices().get(0)); //call execute with the first city in the vertices list as the default source
	}

    // main part of Dijkstra's algorithm. Travel class will call this method.
    // calculates shortest paths and record them.
    public void execute(Vertex source) {
		this.paths = new ArrayList<cityNode>(); //initialize paths list
    	PriorityQueue<cityNode> unvisited = new PriorityQueue<cityNode>(); //min heap holding cities not yet visited
    	
    	//iterate through each Vertex in graph, and add the cities to unvisited queue
    	ListIterator<Vertex> vertexIter = graph.getVertices().listIterator();    	
    	while(vertexIter.hasNext()){
    		Vertex nextVertex = vertexIter.next();
    		cityNode nextCityNode; //sets up a cityNode to store this city and related info
    		
			//if this Vertex = source, the distance from source to source is 0
    		if(nextVertex.getCity().equals(source.getCity())){
    			nextCityNode = new cityNode(nextVertex, 0);
    		}
			//initialize the distance from source to all other cities as infinity (-1 indicates infinite distance)
    		else{
    			nextCityNode = new cityNode(nextVertex, -1);
    		}
    		
    		unvisited.add(nextCityNode);
    	}

    	//while the unvisited queue is not empty
    	while(unvisited.peek() != null){
    		//visit the next city in the queue and add it to the paths list
    		cityNode currNode = unvisited.poll();
    		paths.add(currNode);
    		
    		//if the current city's weight is infinity (-1), then this city and all further cities cannot be reached,
    		//so quit checking
    		if(currNode.getWeight() == -1){
    			break;
    		}
    		
    		//get the current city's neighbors
    		List<Vertex> neighbors = getSimpleNeighbors(currNode.getVertex());
    		ListIterator<Vertex> neighborIter = neighbors.listIterator();
    		
    		//examine possible paths to each direct neighbor of the current city
    		while(neighborIter.hasNext()){
    			Vertex nextNeighbor = neighborIter.next();
    			Edge nextEdge = graph.getEdge(currNode.getVertex().getCity(), nextNeighbor.getCity());
    			int distance = nextEdge.getDistance(); //the distance between the current city and this neighbor city
    			
    			//get the relevant cityNode (from the queue) for this neighbor
    			cityNode neighborNode = getCityNodeFromQueue(unvisited, nextNeighbor);
    			
    			//if neighborNode is not null (we haven't yet actually visited this neighbor)
    			//check if passing through the current node produces a shorter path to this neighbor
    			//than the previous path we stored
    			if(neighborNode != null){
    				//if the weight of the current node + the distance to this neighbor is < this neighbor's current weight
    				if((neighborNode.getWeight() == -1) || (currNode.getWeight() + distance < neighborNode.getWeight())){
    					//we found a new, shorter, path to this neighbor; update the node accordingly
    					neighborNode.setWeight(currNode.getWeight() + distance);
        				neighborNode.setPrevious(currNode);
        				
        				//force the unvisited queue to reorder itself after updating the weight of the node
        				unvisited.remove(neighborNode);
        				unvisited.add(neighborNode);
    				} //end if
    			} //end if
    		} //end while (neighborIter.hasNext())
    	} //end while(unvisited.peek() != null)
    	
    	//add any remaining unvisited (and unreachable) cities to the paths list
    	while(unvisited.peek() != null){
    		paths.add(unvisited.poll());
    	}
    	
    	//calculate and store the path for each node in paths list
    	//if the vertex is reachable from source, the path will include both the source and destination vertices
    	//else, the path will be null
    	for(int i = 0; i < paths.size(); i++){
    		cityNode nextCity = paths.get(i); //the next city to calculate the path for    		
    		cityNode currNode = nextCity; //the current node as we trace the path back
    		
    		//if this node is reachable, add the destination node to the path
    		if(currNode.getPrevious() != null){
    			nextCity.addToPath(currNode.getVertex());
    		}
    		//trace the path back to the source, adding vertices as we go
    		while(currNode.getPrevious() != null){
    			nextCity.addToPath(currNode.getPrevious().getVertex());
    			currNode = currNode.getPrevious();
    		}
    	}
    }
    
    //helper method for execute()
    //returns the cityNode containing the specified Vertex from the specified PriorityQueue
    private cityNode getCityNodeFromQueue(PriorityQueue<cityNode> queue, Vertex vertex){
    	Iterator<cityNode> iter = queue.iterator();
    	while(iter.hasNext()){
    		cityNode nextNode = iter.next();
    		if(nextNode.getVertex().equals(vertex)){
    			return nextNode;
    		}
    	}
    	return null;
    }

    // Returns a list of the neighbored cities.
    // Here neighbored cities are the cities which have a direct route from 
    // the specified city.
    public List<Vertex> getSimpleNeighbors(Vertex node) {
    	LinkedList<Vertex> vertices = new LinkedList<Vertex>(); //copy of vertices list from graph
    	vertices.addAll(graph.getVertices());    	
    	LinkedList<Vertex> neighbors = new LinkedList<Vertex>(); //node's neighbors
    	
    	//check that node is not null; and that node is actually in the list
    	if(node == null)
    			return null;
    	if(!vertices.contains(node))
    			return null;
    	
    	//remove node from list; no need to check if node is its own neighbor
    	vertices.remove(node);
    	
    	//iterate through remaining vertices:
    	ListIterator<Vertex> iter = vertices.listIterator();
    	while(iter.hasNext()){
    		Vertex nextNode = iter.next();
    		//if an edge between node and nextNode exists, nextNode is one of node's neighbors
    		if(graph.hasEdge(node.getCity(), nextNode.getCity()))
    			neighbors.add(nextNode);
    	}
    	 	
    	return neighbors;
    }

    // returns the shortest distance
    // returns -2 if the destination vertex was not found in the paths list
    // Please make sure that the distance here is between
    // "source" (passed in execute()) and the "destination" (specified in getShortestDistance())
    public int getShortestDistance(Vertex destination) {
    	//check that destination is not null
    	if(destination == null){
    		return -2;
    	}
    	
    	//return the weight associated with this city
    	ListIterator<cityNode> iter = paths.listIterator();
    	while(iter.hasNext()){
    		cityNode currNode = iter.next();
    		if(currNode.getVertex().equals(destination)){
    			return currNode.getWeight();
    		}
    	}
    	return -2; //if the Vertex is not found in the shortest paths list, return -2
    }

    // This method returns the path from the source to the selected target and
    // NULL if no path exists    
    public LinkedList<Vertex> getPath(Vertex target) {
    	//check that target is not null
    	if(target == null){
    		return null;
    	}
    	
    	//retrieve the path associated with this target city
    	ListIterator<cityNode> iter = paths.listIterator();
    	while(iter.hasNext()){
    		cityNode currNode = iter.next();
    		if(currNode.getVertex().equals(target)){
    			return currNode.getPath();
    		}
    	}
    	return null; //if the target Vertex is not found in the shortest paths list, return null
    }
    
    
    //unit testing
    /*
	public static void main(String[] args){
    	//set up a new graph
    	String[] cities = {"A", "B", "C", "D", "E", "F"};
    	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    	ArrayList<Edge> edges = new ArrayList<Edge>(); 	
    	//make vertices
    	for(int i = 0; i < cities.length; i++){
    		vertices.add(new Vertex(cities[i]));
    	}
    	Vertex trollVertex = new Vertex("TROLL");
    	//make edges
    	Random randGen = new Random();
    	for(int i = 0; i < cities.length; i++){
    		int distance = Math.abs(randGen.nextInt() % 100);
    		int destI = Math.abs(randGen.nextInt() % cities.length);
    		edges.add(new Edge(cities[i], cities[destI], distance));
    	}
    	
    	//make graph
    	Graph graph = new Graph(vertices, edges);
    	//print out graph
    	System.out.println("Made a new graph.");
    	System.out.println(graph);
    	
    	//make shortest path
    	ShortestPath path = new ShortestPath(graph);
    	System.out.println("Made a new instance of ShortestPath, path, using this graph.\n");
    	
    	//test getSimpleNeighbors()
    	for(int i = 0; i < vertices.size(); i++){
    		System.out.println("Calling path.getSimpleNeighbors(" + vertices.get(i).getCity() + ").");
    		List<Vertex> neighbors = path.getSimpleNeighbors(vertices.get(i));
    		ListIterator<Vertex> iter = neighbors.listIterator();
    		System.out.println("Neighbors = ");
    		while(iter.hasNext()){
    			System.out.print(iter.next().getCity() + " ");
    		}
    		System.out.println("\n");
    	}
    	
    	//test getSimpleNeighbors() with a city not in graph
		System.out.println("Calling path.getSimpleNeighbors(TROLL).");
		List<Vertex> neighbors = path.getSimpleNeighbors(trollVertex);
		if(neighbors != null){
			ListIterator<Vertex> iter = neighbors.listIterator();
			System.out.println("Neighbors = ");
			while(iter.hasNext()){
				System.out.print(iter.next().getCity() + " ");
			}
			System.out.println("\n");
		}
		else{
			System.out.println("Returned null.");
		}
		System.out.println();
		
		//test cityNode's compareTo()
		cityNode node1 = path.new cityNode(new Vertex("U"), 0);
		cityNode node2 = path.new cityNode(new Vertex("I"), -1);
		cityNode node3 = path.new cityNode(new Vertex("J"), 10);
		System.out.println("Testing cityNode's compareTo().");
		System.out.println("Node.compareTo itself: " + node1.compareTo(node1));
		System.out.println("Infinite node compared to itself: " + node2.compareTo(node2));
		System.out.println("Infinite compared to positive: " + node2.compareTo(node1));
		System.out.println("Positive compared to infinite: " + node1.compareTo(node2));
		System.out.println("Positive compared to larger positive: " + node1.compareTo(node3));
		System.out.println("Positive compared to smaller positive: " + node3.compareTo(node1));		
		System.out.println();
		
		//test execute()
		System.out.println("Calling path.execute(" + vertices.get(0).getCity() + ").");
		path.execute(vertices.get(0));
		ListIterator<cityNode> pathsIter = path.paths.listIterator();
		while(pathsIter.hasNext()){
			cityNode next = pathsIter.next();
			if(next.getPrevious() == null){
				System.out.println("City: " + next.getVertex().getCity() + " Weight: " + next.getWeight() + 
						" Previous: " + next.getPrevious());
			}
			else{
				System.out.println("City: " + next.getVertex().getCity() + " Weight: " + next.getWeight() + 
						" Previous: " + next.getPrevious().getVertex().getCity());
			}
		}
		System.out.println();
		
		//test getShortestDistance()
		System.out.println("Testing getShortestDistance().");
		for(int i = 0; i < vertices.size(); i++){
			System.out.println("path.getShortestDistance(" + vertices.get(i).getCity() + ") = " + path.getShortestDistance(vertices.get(i)));
		}
		System.out.println();
		
		//test getShortestDistance() on a non-existent city
		System.out.println("Testing getShortestDistance(TROLL).");
		System.out.println("path.getShortestDistance(TROLL) = " + path.getShortestDistance(trollVertex));
		System.out.println();
		
		//test getPath() on the source city
		System.out.println("Testing path.getPath(" + cities[0] + ").");
		LinkedList<Vertex> pathGotten = path.getPath(vertices.get(0));
		System.out.println("Path = ");		
		ListIterator<Vertex> iter = pathGotten.listIterator();
		while(iter.hasNext()){
			System.out.println(iter.next().getCity());
		}
		System.out.println();
		
		//test getPath()
		System.out.println("Testing path.getPath(" + cities[1] + ").");
		pathGotten = path.getPath(vertices.get(1));
		System.out.println("Path = ");		
		iter = pathGotten.listIterator();
		while(iter.hasNext()){
			System.out.println(iter.next().getCity());
		}
		System.out.println();
		
		//test getPath() on a city which doesn't exist
		System.out.println("Testing path.getPath(TROLL)");
		pathGotten = path.getPath(trollVertex);
		System.out.println("Path = ");
		if(pathGotten == null){
			System.out.println("null");
		}
		else{
			iter = pathGotten.listIterator();
			while(iter.hasNext()){
				System.out.println(iter.next().getCity());
			}
		}
		System.out.println();
    }
	*/
}
