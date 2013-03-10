//Simple edge class
public class Edge<K>{

	private	Vertex<K> origin; 
	private Vertex<K> destination;
	
	public Edge(Vertex<K> origin, Vertex<K> destination){
		setOrigin(origin);
		setDestination(destination);		
	}

	//Methods are pretty self explanatory
	public Vertex<K> getDestination() {
		return destination;
	}

	public void setDestination(Vertex<K> destination) {
		this.destination = destination;
	}

	public Vertex<K> getOrigin() {
		return origin;
	}

	public void setOrigin(Vertex<K> origin) {
		this.origin = origin;
	}
	//Checks if 2 edges are the same, i.e. their origins and destinations are the same
	public boolean equals(Edge<K> e){
		if(e.getOrigin().equals(origin)&&e.getDestination().equals(destination))
			return true;
		return false;
	}
}
