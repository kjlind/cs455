package cs455.overlay.dijkstra;
/**
 * Casey Anderson
 * 
 * Graph is the class containing information about your graph
 * such as vertices and edges
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Graph {
    private List<Vertex> _vertices;
    private List<Edge> _edges;
    private int _numVertices;
    private int _numEdges;
    

    // constructors
    public Graph(List<Vertex> vertices, List<Edge> edges) {
    	_vertices = vertices;
    	_edges = edges;
    	_numVertices = _vertices.size();
    	_numEdges = _edges.size();   	
    }
    
    public Graph(){
    	_numVertices = 0;
    	_numEdges = 0;
    	_vertices = new ArrayList<Vertex>();;
    	_edges = new ArrayList<Edge>();
    }
    
    // sets multiple vertices
    public void setVertices(List<Vertex> vertices){
    	getVertices().addAll(vertices);
    	_numVertices += vertices.size();
    }
    
    // sets multiple edges
    public void setEdges(List<Edge> edges){
		getEdges().addAll(edges);
		_numEdges += edges.size();
    }

    // checks if this graph has this vertex with specified name
    public boolean hasVertex(String name){
    	
		Iterator<Vertex> vertexIter = getVertices().iterator();
		
		while(vertexIter.hasNext())
		{
			if(vertexIter.next().getCity().equals(name))
				return true;
		}
		return false;
    }

    // returns the vertex described with specified name
    //returns null if not found
    public Vertex getVertex(String name){
		if(!hasVertex(name))
			return null;
		
		Iterator<Vertex> vertexIter = getVertices().iterator();
		
		while(vertexIter.hasNext())
		{
			Vertex current = vertexIter.next();
			if(current.getCity().equals(name))
				return current;
		}
		return null;
    }

    // checks if this graph has this edge. 
    // please make sure that this graph is undirected.
    public boolean hasEdge(String source, String dest, int dist){
		
    	Iterator<Edge> edgeIter = getEdges().iterator();
		
		while(edgeIter.hasNext())
		{
			if(edgeIter.next().equals(source, dest, dist))
				return true;
		}
		return false;
    }
    
    public Edge getEdge(String source, String dest)
    {
    	Iterator<Edge> edgeIter = getEdges().iterator();
		
		while(edgeIter.hasNext())
		{
			Edge current = edgeIter.next();
			if(current.equals(source, dest))
				return current;
		}
		return null;
    }
    
    public boolean hasEdge(String source, String dest)
    {
    	Iterator<Edge> edgeIter = getEdges().iterator();
		
		while(edgeIter.hasNext())
		{
			if(edgeIter.next().equals(source, dest))
				return true;
		}
		return false;
    }
	   
    // returns the edge from the edge list
    //returns null if edge doesn't exist
    public Edge getEdge(Edge thisEdge){
    	
    	Iterator<Edge> edgeIter = getEdges().iterator();
		
		while(edgeIter.hasNext())
		{
			Edge current = edgeIter.next();
			
			if(current.equals(thisEdge))
				return current;
		}
		return null;
    }
	    
    // returns the list of vertices
    public List<Vertex> getVertices() {		
		return _vertices;
    }
    
    // returns the list of edges
    public List<Edge> getEdges() {
		return _edges;
    }
	
	public int getNumVertices()
	{
		return _numVertices;
	}
	
	public int getNumEdges()
	{
		return _numEdges;
	}
	
	public String toString()
	{
		String graph = "";
		
		//Print vertices:
		graph += "Vertices:\n";
		
		Iterator<Vertex> vertexIter = getVertices().iterator();
		
		while(vertexIter.hasNext())
			graph += vertexIter.next().getCity() + "\n";
		
		graph += "\n";
		
		//Print edges:
		graph += "Edges:\n";
		
		Iterator<Edge> edgeIter = getEdges().iterator();
		
			while(edgeIter.hasNext())
			{
				Edge current = edgeIter.next();
				graph += current.getCity1() + " ";
				graph += current.getCity2() + " ";
				graph += current.getDistance() + "\n";
			}
		
			graph += "\n";
			
			return graph;
	}
	
}