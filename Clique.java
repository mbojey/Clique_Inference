
import java.util.ArrayList;
public class Clique<K> {

	public ArrayList<Vertex<K>> members = new ArrayList<>(1);
	private int weight = 1;
	private Potential<K> phi_x;
	private boolean mark = false;
	
	public Clique(){}
	
	//Constructor that takes an initial clique that may be added to.
	public Clique(ArrayList<Vertex<K>> initial_members){
		members.addAll(initial_members);
		for(int i = 0; i < members.size(); i++)
			weight *= members.get(i).getNum_states();
	}
	
	//Takes a vertex and adds it to the clique.
	public boolean add_member(Vertex<K> new_member){
		if(isMember(new_member))
		return false;
		members.add(new_member);
		weight *= new_member.getNum_states();
		return true;
	}	
	
	//Checks if a given vertex is a member of the clique.
	public boolean isMember(Vertex<K> v){
		for(int i = 0; i < members.size(); i++)
			if(members.get(i).equals(v))
				return true;
		return false;
	}
	
	//Returns a list of all members.
	public ArrayList<Vertex<K>> getMembers(){
		ArrayList<Vertex<K>> temp = new ArrayList<>();
		temp.addAll(members);
		return temp;
	}
	
	//Checks if a given clique is a subset of this clique i.e. a.contains_subset(b) is b is a subset of a.
	public boolean contains_subset(Clique<K> c){
		ArrayList<Vertex<K>> temp1 = new ArrayList<>();
		ArrayList<Vertex<K>> temp =  new ArrayList<>();
		temp.addAll(c.getMembers());
		temp1.addAll(temp);
		int counter = 0;
		for(int i = 0; i < temp.size(); i++)
			for(int j = 0; j < members.size(); j++)
				if(temp.get(i).equals(members.get(j))){
					temp1.remove(i-counter);
					j = members.size();
					counter++;
				}
		return temp1.isEmpty();
	}
	
	//Returns as an arraylist the intersection of 2 subsets, i.e. vertices that are present in both.
	public ArrayList<Vertex<K>> intersection (Clique<K> c){
		ArrayList<Vertex<K>> temp = c.getMembers();
		ArrayList<Vertex<K>> result = new ArrayList<>();
		for(int i = 0; i < members.size(); i++)
			for(int j = 0; j < temp.size(); j++)
				if(members.get(i).equals(temp.get(j)))
					result.add(members.get(i));
		return result;	
	}
	
	//Returns as an arraylist the intersection of 2 subsets, i.e. vertices that are present in both.
	public ArrayList<Vertex<K>> not_in_both (Sepset<K> c){
		ArrayList<Vertex<K>> temp = c.getMembers();
		ArrayList<Vertex<K>> result = new ArrayList<>();
		boolean in_both = false;
		for(int i = 0; i < members.size(); i++){
			in_both = false;
			for(int j = 0; j < temp.size(); j++)
				in_both |= members.get(i).equals(temp.get(j));
			if(!in_both)
			result.add(members.get(i));
		}	
		return result;	
	}
	
	public ArrayList<Vertex<K>> not_current_vertex(Vertex<K> vertex){
		ArrayList<Vertex<K>> result = new ArrayList<>();
		result.addAll(members);
		for(int i = 0; i < result.size(); i++){
			if(result.get(i).equals(vertex)){
				result.remove(i);
				break;
			}
		}
		return result;
	}
	//Returns the weight of the clique, weight is the product of the number of states of the variables in a clique
	public int getWeight(){
		return weight;
	}
	
	//Sets phi_x to initially be a potential with 3 dimensions and all values == 1;
	public boolean initializePhi_x() {
		if(members.size() == 3){
			double[][][] temp_array = new double[members.get(0).getNum_states()][members.get(1).getNum_states()][members.get(2).getNum_states()];
			for(int i = 0; i < temp_array.length; i++)
				for(int j = 0; j < temp_array[0].length; j++)
					for(int k = 0; k < temp_array[0][0].length; k++)
						temp_array[i][j][k] = 1;
			setPhi_x(new Potential<K>(temp_array, members.get(0), members.get(1), members.get(2)));
		}
		else{
			double[][]temp_array = new double[members.get(0).getNum_states()][members.get(1).getNum_states()];
			for(int i = 0; i < temp_array.length; i++)
				for(int j = 0; j < temp_array[0].length; j++)
						temp_array[i][j] = 1;
			setPhi_x(new Potential<K>(temp_array, members.get(0), members.get(1)));
		}
		return true;
	}
	
	
	//Checks if a given clique is equal to the current clique by checking if the members are the same
	public boolean equals(Clique<K> clique){
		if(clique.members.size() != members.size())
			return false;
		ArrayList<Vertex<K>> temp1 = new ArrayList<>();
		ArrayList<Vertex<K>> temp =  new ArrayList<>();
		ArrayList<Vertex<K>> temp2 = new ArrayList<>();
		temp.addAll(clique.getMembers());
		temp1.addAll(temp);
		temp2.addAll(members);
		int counter = 0;
		for(int i = 0; i < temp.size(); i++)
			for(int j = 0; j < members.size(); j++)
				if(temp.get(i).equals(members.get(j))){
					temp1.remove(i-counter);
					temp2.remove(i-counter);
					j = members.size();
					counter++;
				}
		return temp1.isEmpty()&&temp2.isEmpty();
	}

	//Finds the first position of a vertex in an ArrayList of vertices, returns -1 is it is not there. Just realized that there is a method that does 
	//exactly this but practice is good right??
	public int find_position(Vertex<K> vertex) {
		for(int i = 0; i < members.size(); i++)
			if(members.get(i).equals(vertex))
				return i;
		return -1;	
	}

	//Returns phi_x
	public Potential<K> getPhi_x() {
		return phi_x;
	}

	//Sets phi_x to a new Potential, used in evidence initializing and propagation.
	public void setPhi_x(Potential<K> phi_x) {
		this.phi_x = phi_x;
	}

	
	//Returns the current value of mark.
	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}
	
	public String toString(){
		String result = "";
		for(int i = 0; i < members.size(); i++)
			result += members.get(i).toString() + ",";
		return result;
	}

	
}
