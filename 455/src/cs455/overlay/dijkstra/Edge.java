package cs455.overlay.dijkstra;
/**
 * Casey Anderson
 * 
 * Edge is the class to define a route between cities
 * Design your edge. What information do you need to specify an Edge? 
 * Please add your methods and properties.
 */

public class Edge  {
	
    private int _distance;
    private String _city1;
    private String _city2;
    
    public Edge(String city1, String city2, int distance)
    {
    	_city1 = city1;
    	_city2 = city2;
    	_distance = distance;
    }
    
    public String getCity1()
    {
    	return _city1;
    }
    
    public String getCity2()
    {
    	return _city2;
    }
    
    public int getDistance()
    {
    	return _distance;
    }
    
    public boolean equals(String source, String dest, int dist)
    {
    	if(((getCity1().equals(source) && getCity2().equals(dest)) || (getCity2().equals(source) && getCity1().equals(dest))) && getDistance() == dist)
    		return true;
    	else
    		return false;
    }
    
    public boolean equals(Edge edge)
    {
    	if(((getCity1().equals(edge.getCity1()) && getCity2().equals(edge.getCity2())) || (getCity2().equals(edge.getCity1()) && getCity1().equals(edge.getCity2()))) && getDistance() == edge.getDistance())
    		return true;
    	else
    		return false;
    }
    
    public boolean equals(String source, String dest)
    {
    	if((getCity1().equals(source) && getCity2().equals(dest)) || (getCity2().equals(source) && getCity1().equals(dest)))
    		return true;
    	else
    		return false;
    }
	
}
