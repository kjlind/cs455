package cs455.overlay.dijkstra;
/**
 * Casey Anderson
 * 
 * Vertex is the class to define a city.
 * Design your vertex.
 * What information do you need to describe a city here?
 * Please add your properties and methods.
 */

public class Vertex {
    
	private String _city;
//	private int _degree;
	
	public Vertex(String city)
	{
		_city = city;
//		_degree = 0;
	}
	
	public String getCity()
	{
		return _city;
	}
	
/*	public void setDegree(int degree)
	{
		_degree = degree;
	}
	
	public int getDegree()
	{
		return _degree;
	}
	
	public void incrementDegree()
	{
		_degree++;
	}
*/	
	public boolean equals(Vertex vertex)
	{
		return this.getCity().equals(vertex.getCity());
	}
	
	@Override
	public boolean equals(Object other){
	    if(!(other instanceof Vertex)){
	        return false;
	    }
	    Vertex vertex = (Vertex) other;
        return this.getCity().equals(vertex.getCity());
	}
	
}