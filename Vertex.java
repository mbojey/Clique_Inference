import java.util.Random;
import java.util.ArrayList;
public class Vertex<K> {
	private int num_parents = 0; //Assume no parents
	private int num_states;
	private int num_parent_states = 1; //Even though assume no parents, this makes the way I calculated cond. prob. possible
	public ArrayList<Vertex<K>> parents = new ArrayList<Vertex<K>>(1);
	public ArrayList<Vertex<K>> children = new ArrayList<Vertex<K>>(1);
	public ArrayList<Integer> parent_state = new ArrayList<Integer>(1);
	private K value; 
	public Potential<K> probabilities;
	public Potential<K> likelihood;
	private Random rand = new Random();
	
	//Constructor, simple enough, takes a value and number of possible states and makes a vertex
	public Vertex(K value, int states){
		setValue(value);
		setNum_states(states);
	}
	
	//Sets the probabilities of the vertex.  Assumes no parents if not given prior probabilities.
	public void setprobabilities(){
		double[] temp =  new double[num_states];
		for(int i = 0; i < temp.length; i++){
				temp[i] = rand.nextDouble();
			normalize(temp);
		}
		probabilities = new Potential<K>(temp, this);
	}
	
	//Given probability values sets the potential for a vertex with no parents
	public void setprobabilities(double[] given_prob){
		probabilities = new Potential<K>(given_prob, this);
	}
	
	//Given probability values sets the potential for a vertex with one parents
	public void setprobabilities(double[][] given_prob){
		probabilities = new Potential<K>(given_prob, parents.get(0),this);
	}
	
	//Given probability values sets the potential for a vertex with two parents
	public void setprobabilities(double[][][] given_prob){
		probabilities = new Potential<K>(given_prob, parents.get(0), parents.get(1), this);
	}
	
	//Makes certain that for a given prior/condition the probability of all possible states adds to one.  Same method as in Value Iterataion Model
	private void normalize(double[] array){
		double sum = 0.0f;
		for(int position = 0; position < array.length; position += num_states){
			for(int i = position;i < num_states ;i++)
				sum += array[i];
			for(int i = position;i<num_states;i++)
				array[i]=array[i]/sum;
		}
	}
	
	//Adds a parent, passed in, to the list of parents and multiplies the  TOTALnumber of parent states by the number of states of the GIVEN parent
	//states.  This is crucial to making sure that the conditional probability table is big enough and why the num_parent_states is intially 1
	public void addparent(Vertex<K> v){
		parents.add(v);
		num_parents++;
		num_parent_states *= v.getNum_states();
		parent_state.add(v.getNum_states());
	}
	public void addchild(Vertex<K> v){
		children.add(v);
	}
	
	public boolean equals(Vertex<K> v){
		return(value.equals(v.getValue())&&num_states == v.getNum_states());
	}
	//Everything else is just setters/getters.  Simple enough not gonna comment cause I am lazy and tired.
	public K getValue() {
		return value;
	}

	public void setValue(K value) {
		this.value = value;
	}

	public int getNum_parents() {
		return num_parents;
	}

	public void setNum_parents(int num_parents) {
		this.num_parents = num_parents;
	}

	public int getNum_states() {
		return num_states;
	}

	public void setNum_states(int num_states) {
		this.num_states = num_states;
	}

	public void initialize_likelihood() {
		double[] temp = new double[num_states];
		for(int i = 0; i < temp.length; i++)
			temp[i] = 1;
		likelihood = new Potential<K>(temp, this);
	}

	public void set_likelihood(int state) {
		double[] temp = new double[num_states];
		for(int i = 0; i < temp.length; i++)
			temp[i] = 0;
		temp[state] = 1;
		likelihood = new Potential<K>(temp, this);
	}
	
}
