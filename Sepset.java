
import java.util.ArrayList;
public class Sepset<K> {

	private ArrayList<Vertex<K>> members = new ArrayList<>();
	private Clique<K> origin = new Clique<K>();
	private Clique<K> destination = new Clique<K>();
	private int mass;
	private int cost = 0;
	private Potential<K> phi_x;
	
	public Sepset(){}
	//Constructor that takes a list of vertices and set members to this list of vertices
	public Sepset(ArrayList<Vertex<K>> intersection, Clique<K> origin, Clique<K> destination){
		members.addAll(intersection);
		this.setOrigin(origin);
		this.setDestination(destination);
		setMass(members.size());
		cost += origin.getWeight() * destination.getWeight(); 
	}

	public Clique<K> getOrigin() {
		return origin;
	}

	public void setOrigin(Clique<K> origin) {
		this.origin = origin;
	}

	public Clique<K> getDestination() {
		return destination;
	}

	public void setDestination(Clique<K> destination) {
		this.destination = destination;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	
	public int getCost() {
		return cost;
	}

	public ArrayList<Vertex<K>> getMembers(){
		return members;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	//Fills phi_x with all the same value, specifically 1's
	public void initializePhi_x() {
		if(members.size() == 1){
			double[]temp_array = new double[members.get(0).getNum_states()];
			for(int i = 0; i < temp_array.length; i++)
					temp_array[i] = 1;
			setPhi_x(new Potential<K>(temp_array, members.get(0)));
		}
		else{
		double[][]temp_array = new double[members.get(0).getNum_states()][members.get(1).getNum_states()];
		for(int i = 0; i < temp_array.length; i++)
			for(int j = 0; j < temp_array[0].length; j++)
				temp_array[i][j] = 1;
		setPhi_x(new Potential<K>(temp_array, members.get(0), members.get(1)));
		}
	}
	
	
	public Potential<K> getPhi_x() {
		return phi_x;
	}
	public void setPhi_x(Potential<K> phi_x) {
		this.phi_x = phi_x;
	}
	
	public String toString(){
		String result = "";
		result += "Origin" + origin.toString() + ",";
		result += "Destination" + destination.toString();
		return result;
	}
	
}
