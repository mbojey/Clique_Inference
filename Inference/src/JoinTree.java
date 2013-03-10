import java.util.ArrayList;
public class JoinTree<K> {

	private ArrayList<Sepset<K>> sepsets = new ArrayList<>(1);
	private ArrayList<Clique<K>> cliques = new ArrayList<>(1);
	private ArrayList<Vertex<K>> vertices = new ArrayList<>(1);
	private int total_states = 0;
	
	//Constructor, builds a join tree from a list of cliques and a list of sepsets
	public JoinTree(ArrayList<Clique<K>> cliques, ArrayList<Sepset<K>> sepsets, ArrayList<Vertex<K>> vertices){
		this.vertices.addAll(vertices);
		for(int i = 0; i < vertices.size(); i++)
			total_states += vertices.get(i).getNum_states();
		Sepset<K> temp = new Sepset<K>();
		for(int i = 0; i < cliques.size()-1; i++){
			if(unique_max_mass(sepsets)){
				temp = find_max_mass(sepsets);
				if(!(this.cliques.contains(temp.getOrigin())&&this.cliques.contains(temp.getDestination()))){
					if(!(this.cliques.contains(temp.getOrigin())))
						this.cliques.add(temp.getOrigin());
					if(!(this.cliques.contains(temp.getDestination())))
						this.cliques.add(temp.getDestination());
					this.sepsets.add(temp);
				}else i--;
				sepsets.remove(temp);
			}else{
				temp = find_min_cost(sepsets);
				if(!(this.cliques.contains(temp.getOrigin())&&this.cliques.contains(temp.getDestination()))){
					if(!(this.cliques.contains(temp.getOrigin())))
						this.cliques.add(temp.getOrigin());
					if(!(this.cliques.contains(temp.getDestination())))
						this.cliques.add(temp.getDestination());
					this.sepsets.add(temp);
				}else i--;
				sepsets.remove(temp);
			}
		}
	}
	
	//Takes the join tree and makes it consistent by initializing and propagating the belief tables properly??
	public boolean makeConsistent(){
		return initialize() && propagate();
		
	}
	
	//Finds the max mass in a list of sepsets and telle user if the max is unique.
	private boolean unique_max_mass(ArrayList<Sepset<K>> sepsets) {
		int max = 0;
		int position = 0;
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).getMass() > max){
				max = sepsets.get(i).getMass();
				position = i;
			}
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).getMass() == max && i!= position)
				return false;
		return true;
	}
	
	//Finds the sepset from a list of sepsets that has the highest mass
	private Sepset<K> find_max_mass(ArrayList<Sepset<K>> sepsets){
		int max = 0;
		Sepset<K> result = new Sepset<K>();
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).getMass() > max){
				max = sepsets.get(i).getMass();
				result = sepsets.get(i);
			}
		return result;
	}
	
	//Finds the sepset from a list of sepsets that has the lowest cost
	private Sepset<K> find_min_cost(ArrayList<Sepset<K>> sepsets){
		int min = Integer.MAX_VALUE;
		Sepset<K> result = new Sepset<K>();
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).getCost() < min){
				min = sepsets.get(i).getCost();
				result = sepsets.get(i);
			}
		return result;
	}
	
	//Fills phi_x for all cliques in the join tree and sets phi_x for all sepsets to 1.  This method seems to not work, but maybe I am just
	//not understanding properly
	private boolean initialize(){
		setPhi();		
		for(int k = 0; k < vertices.size(); k++)
			fillFamily(vertices.get(k));
		return true;
	}
	
	//Does the actual filling of the phi_x for the cliques and sepsets to 1
	private void setPhi() {
		for(int i = 0; i < cliques.size(); i++)
			cliques.get(i).initializePhi_x();
		for(int j = 0; j < sepsets.size(); j++)
			sepsets.get(j).initializePhi_x();
	}
	
	//Takes each vertex and changes phi_x for it's families clique.
	private void fillFamily(Vertex<K> vertex) {
		Clique<K> temp = new Clique<K>(vertex.parents);
		temp.add_member(vertex);
		temp.setPhi_x(vertex.probabilities);
		int i = 0;
		while(!cliques.get(i).contains_subset(temp))
			i++;
		cliques.get(i).setPhi_x(cliques.get(i).getPhi_x().multiply_potentials(temp.getPhi_x()));

	}
	
	//Method called by the user to do global propagation
	private boolean propagate(){
		falsify();
		collect_evidence(cliques.get(0));
		falsify();
		distribute_evidence(cliques.get(0));
		normalize();
		return true;
	}
	
	//Sets all clique marks to false
	private void falsify(){
		for(int i = 0; i < cliques.size(); i++)
			cliques.get(i).setMark(false);
	}
	
	//Collects evidence through a series of recursive message passes.a Calls collect evidence on any unmarked neighbors
	private void collect_evidence(Clique<K> clique) {
		clique.setMark(true);
		ArrayList<Clique<K>> neighbors = neighbors(clique);
		for(int i = 0; i < neighbors.size(); i++)
			if(!neighbors.get(i).isMark())
				collect_evidence(cliques.get(find_in_cliques(neighbors.get(i))), cliques.get(find_in_cliques(clique)));
		
	}

	//Calls collect evidence on any unmarked neighbors and then passes a message from the current clique to the clique which called the method
	//in the first place
	private void collect_evidence(Clique<K> new_clique, Clique<K> caller_clique) {
		new_clique.setMark(true);
		ArrayList<Clique<K>> neighbors = neighbors(new_clique);
		for(int i = 0; i < neighbors.size(); i++)
			if(!neighbors.get(i).isMark())
				collect_evidence(cliques.get(find_in_cliques(neighbors.get(i))), cliques.get(find_in_cliques(new_clique)));
		message_pass(new_clique, caller_clique);
		
	}
	
	//Distributes evidence through a series of recursive message passes
	private void distribute_evidence(Clique<K> clique) {
		clique.setMark(true);
		ArrayList<Clique<K>> neighbors = neighbors(clique);
		for(int i = 0; i < neighbors.size(); i++)
			if(!neighbors.get(i).isMark()){
				message_pass(clique, cliques.get(find_in_cliques(neighbors.get(i))));
				distribute_evidence(cliques.get(find_in_cliques(neighbors.get(i))));
			}
	}
	
	//Method that does the message passing, don't know how to do this yet, maybe soon
	private void message_pass(Clique<K> passer, Clique<K> reciever) {
		Sepset<K> connection = find_sepset(passer, reciever);
		Potential<K> old_potential = connection.getPhi_x();
		Potential<K> new_potential = passer.getPhi_x();
		ArrayList<Vertex<K>> temp_list = passer.not_in_both(connection);
		for(int i = 0; i < temp_list.size(); i++)
			new_potential = new_potential.sum_out(temp_list.get(i));
		sepsets.get(find_in_sepsets(connection)).setPhi_x(new_potential);
		Potential<K> ratio = new_potential;
		ratio = ratio.divide(old_potential);
		cliques.get(find_in_cliques(reciever)).setPhi_x(cliques.get(find_in_cliques(reciever)).getPhi_x().multiply_potentials(ratio));
	}

	private int find_in_sepsets(Sepset<K> sepset) {
		int position = 0;
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).equals(sepset)){
				position = i;
				break;
			}				
		return position;
	}

	//Given two cliques, known to be connected by a sepset in 'sepsets' finds that sepset;
	private Sepset<K> find_sepset(Clique<K> passer, Clique<K> reciever) {
		for(int i = 0; i < sepsets.size(); i++){
			if((sepsets.get(i).getOrigin().equals(passer)&&sepsets.get(i).getDestination().equals(reciever))||
				(sepsets.get(i).getOrigin().equals(reciever)&&sepsets.get(i).getDestination().equals(passer)))
				return sepsets.get(i);
		}
		return null;
		
	}

	//Finds a given clique in the list of cliques
	private int find_in_cliques(Clique<K> clique) {
		int position = 0;
		for(int i = 0; i < cliques.size(); i++)
			if(cliques.get(i).equals(clique)){
				position = i;
				break;
			}				
		return position;
	}

	//Finds all cliques connected to a given clique using sepsets
	private ArrayList<Clique<K>> neighbors(Clique<K> clique) {
		ArrayList<Clique<K>> neighbors = new ArrayList<>(1);
		for(int i = 0; i < sepsets.size(); i++)
			if(sepsets.get(i).getOrigin().equals(clique))
				neighbors.add(sepsets.get(i).getDestination());
			else if(sepsets.get(i).getDestination().equals(clique))
				neighbors.add(sepsets.get(i).getOrigin());
		return neighbors;
	}

	//Normalizes cliques and sepsets making sure that the total probability always adds to one.
	private boolean normalize(){
		return normalize_cliques() && normalize_sepsets();
	}

	private boolean normalize_sepsets() {
		for(int s = 0; s < sepsets.size(); s++)
			sepsets.get(s).setPhi_x(sepsets.get(s).getPhi_x().normalize());
		return true;
	}

	private boolean normalize_cliques() {
		for(int c = 0; c < cliques.size(); c++)
			cliques.get(c).setPhi_x(cliques.get(c).getPhi_x().normalize());
		return true;
	}
	
	//Returns the probability that a given value is in it's states.  This is the whole point of the thing.
	public double[] find_probability(Vertex<K> vertex){
		double[] result = new double[vertex.getNum_states()];
		for(int i  = 0; i < cliques.size(); i++){
			if(cliques.get(i).find_position(vertex) != -1)
				result = cliques.get(i).getPhi_x().marginalize(vertex).getOne_dimension();
		}
		return result;
	}

	//Returns the probability that any value is in any state
	public double[] find_all_probabilities(){
		double[] result = new double[total_states];
		int counter = 0;
		double[] temp;
		for(int i = 0; i < vertices.size(); i++){
			temp = find_probability(vertices.get(i));
			for(int j = 0; j < temp.length; j++)
				result[counter++] = temp[j];
		}
		return result;
	}

}
