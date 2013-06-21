
import java.util.ArrayList;
public class Bayes_net{
	public ArrayList<Vertex<String>> vertices;
	public ArrayList<Edge<String>> edges;
	public ArrayList<Edge<String>> moraledges;
	public ArrayList<Clique<String>> cliques;
	private int counter = 0; //Keeps track of how many vertices are in the net. not really need just makes some things easier.
	
	
	//Constructor that takes no parameters because I want the net to be any size.
	public Bayes_net(){
		this.vertices = new ArrayList<Vertex<String>>(1);
		this.edges = new ArrayList<Edge<String>>(1);
	}
	
	//Adds a vertex given a value, the v, and a number of states the vertex can take.  Makes sure that the vertex is not already in the
	//net before adding it.  Then calls a method to generate prior probabilities for the new vertex.
	public boolean add_vertex(String v, int states){
		if(vertex (v, states))
			return false;
		vertices.add(new Vertex<String>(v, states));
		fillprobabilities(vertices.get(counter));
		counter++;
		return true;
	}
	
	//Adds a vertex given a value, the v, and a number of states the vertex can take.  Makes sure that the vertex is not already in the
	//net before adding it.  Then calls a method to fill prior probabilities for the new vertex with given values.
	public boolean add_vertex(String v, int states, double[] priors){
		if(vertex (v, states))
			return false;
		vertices.add(new Vertex<String>(v, states));
		fillprobabilities(vertices.get(counter), priors);
		counter++;
		return true;
	}
	
	//Adds an edge from v to w, making sure that the number of states match with what should be in the net.  Then updates the family relationships i.e.
	//v is now a parent of w, and then updates the probability table for w as it now has a new parent, v.  Returns false if it cannot find the vertices
	//in the net.
	public boolean add_edge(String v, int states1, String w, int states2){
		Vertex<String> y = new Vertex<String>(w, states2);
		fillprobabilities(y);
		if(vertex(v, states1)&&vertex(w, states2)){
			if(!edge(v,states1,w,states2)){
				int parent = findVertex(v, states1);
				int child = findVertex(w, states2);
				parent(vertices.get(parent),vertices.get(child));
				fillprobabilities(vertices.get(child));
				Edge<String> temp  = new Edge<String>(vertices.get(parent),y);	
				edges.add(temp);
				return true;
			}
		}
		return false;
	}
	
	//Adds an edge from v to w, making sure that the number of states match with what should be in the net.  Then updates the family relationships i.e.
	//v is now a parent of w, and then updates the probability table for w as it now has a new parent, v. with a given table. 
	//Returns false if it cannot find the vertices in the net.	
	public boolean add_edge(String v, int states1, String w, int states2, double[][] conditionals){
		if(vertex(v, states1)&&vertex(w, states2)){
			if(!edge(v,states1,w,states2)){
				int parent = findVertex(v, states1);
				int child = findVertex(w, states2);
				parent(vertices.get(parent),vertices.get(child));
				fillprobabilities(vertices.get(child), conditionals);
				Edge<String> temp  = new Edge<String>(vertices.get(parent),vertices.get(child));	
				edges.add(temp);
				return true;
			}
		}
		return false;
	}
	
	//Adds an edge from v to w, making sure that the number of states match with what should be in the net.  Then updates the family relationships i.e.
	//v is now a parent of w, and then updates the probability table for w as it now has a new parent, v. with a given table. 
	//Returns false if it cannot find the vertices in the net.  This method is only used when adding a second parent to the child.
	public boolean add_edge(String v, int states1, String w, int states2, double[][][] conditionals){
		if(vertex(v, states1)&&vertex(w, states2)){
			if(!edge(v,states1,w,states2)){
				int parent = findVertex(v, states1);
				int child = findVertex(w, states2);
				parent(vertices.get(parent),vertices.get(child));
				fillprobabilities(vertices.get(child), conditionals);
				Edge<String> temp  = new Edge<String>(vertices.get(parent),vertices.get(child));	
				edges.add(temp);
				return true;
			}
		}
		return false;
	}
	
	//Tries to find a given value/states combination in the net, returns true or false if it finds one or not.
	public boolean vertex(String v, int states){
		for(int i = 0; i < vertices.size(); i++){
			if(v.equals(vertices.get(i).getValue())&&states==vertices.get(i).getNum_states())
				return true;
		}
		return false;
	}
	
	//Tries to find a given value/states combination in the net, returns the position in the array list of the found vertex or -1 if it cannot find it.
	public int findVertex(String v, int states){
		for(int i = 0; i < vertices.size(); i++){
			if(v.equals(vertices.get(i).getValue())&&states==vertices.get(i).getNum_states())
				return i;
		}
		return -1;
	}
	
	//Tries to find a given edge in the graph from v to w and with the appropriate number of states. T/F if it finds one or not.  
	public boolean edge(String v, int states1, String w, int states2){
		Vertex<String> x = new Vertex<>(v, states1);
		Vertex<String> y = new Vertex<String>(w, states2);
		if(vertex(v, states1)&&vertex(w, states2)){
			Edge<String> temp = new Edge<String>(x,y);
			for(int i = 0; i < edges.size(); i++)
				if(temp.equals(edges.get(i)))
					return true;				
		}
		return false;
	}
	
	//Adds v to the list of w's parents
	private void parent(Vertex<String> v, Vertex<String> w){
		w.addparent(v);
		v.addchild(w);
	}
		
	//Fills the probabilities of v.
	private void fillprobabilities(Vertex<String> v){
		v.setprobabilities();
	}
	
	//Fills the probabilities of V with pre-defined values
	private void fillprobabilities(Vertex<String> v, double[] priors){
		v.setprobabilities(priors);
	}
	
	//Fills the probabilities of V with pre-defined values
	private void fillprobabilities(Vertex<String> v, double[][] priors){
		v.setprobabilities(priors);
	}
		
	//Fills the probabilities of V with pre-defined values
	private void fillprobabilities(Vertex<String> v, double[][][] priors){
		v.setprobabilities(priors);
	}
	
	
	//This method makes all the changes necessary to create the secondary structure and then returns it
	public JoinTree convertToJoinTree(){
		makeMoral();
		cliques = new ArrayList<Clique<String>>();
		triangulate(moraledges, vertices);
		JoinTree result = makeJoinTree();
		return result;
	}
	
	//Takes the Bayes-net and makes a moral graph by removing edge directions and connecting parents.  Stores the result in Moraledges.  Note that
	// at this point vertices and their probabilities do no change at all, only the edges are changed.
	public boolean makeMoral(){
		moraledges = new ArrayList<Edge<String>>(1);
		moraledges.addAll(edges);
		Edge<String> temp;
		int size = edges.size();
		for(int i = 0; i < size; i++){
			temp = new Edge<String>(edges.get(i).getDestination(), edges.get(i).getOrigin());
			moraledges.add(temp);
		}
		connectparents();
		return true;
	}
	
	//Connects parents by looking for edges with a common destination and connecting the parents of that destination.
	private void connectparents(){
		for(int i = 0; i < edges.size(); i++){
			for(int j = i+1; j < edges.size(); j++){
				if(edges.get(i).getDestination().equals(edges.get(j).getDestination())){
					Edge<String> join = new Edge<String>(edges.get(i).getOrigin(),edges.get(j).getOrigin());
					if(!moraledges.get(0).equals(join))
					moraledges.add(join);
					join = new Edge<String>(edges.get(j).getOrigin(),edges.get(i).getOrigin());
					if(!moraledges.get(0).equals(join))
					moraledges.add(join);
				}					
			}
		}
	}
	
	//Checks if two given vertices form a Moral edge.
	public boolean Moraledge(String v, int states1, String w, int states2){
		Vertex<String> x = new Vertex<>(v, states1);
		Vertex<String> y = new Vertex<String>(w, states2);
		if(vertex(v, states1)&&vertex(w, states2)){
			Edge<String> temp = new Edge<String>(x,y);
			for(int i = 0; i < moraledges.size(); i++)
				if(temp.equals(moraledges.get(i)))
					return true;				
		}
		return false;
	}
	
	//Given 2 vertices checks if they are a Moraledge
	private boolean Moraledge(Vertex<String> v, Vertex<String> w){
		Edge<String> temp = new Edge<String>(v,w);
		for(int i = 0; i < moraledges.size(); i++)
			if(temp.equals(moraledges.get(i)))
				return true;
		return false;
	}
	
	//Triangulates the moral graph
	public boolean triangulate(ArrayList<Edge<String>> Moraledges_current, ArrayList<Vertex<String>> vertices_current){
		if(vertices_current.isEmpty())
			return true;
		ArrayList<Vertex<String>> new_vert = new ArrayList<>();
		ArrayList<Edge<String>> new_edge = new ArrayList<>();
		int[] num_edges = num_new_edges(Moraledges_current, vertices_current);
		if(unique_min(num_edges)){
			int position = position_of_min(num_edges);
			Moraledges_current = make_clusters(vertices_current.get(position), Moraledges_current);
			new_edge.addAll(remove_vertices(Moraledges_current, vertices_current.get(position)));
			new_vert.addAll(thing(vertices_current, position));
			triangulate(new_edge, new_vert);
			return true;
		}
		else{
			ArrayList<Integer> positions = positions_of_min(num_edges);
			int[] weights = new int[positions.size()];
			for(int l = 0; l < positions.size(); l++){
				weights[l] = cluster_weight(vertices_current.get(positions.get(l)), Moraledges_current);
			}
			int position = position_of_min(weights);
			Moraledges_current = make_clusters(vertices_current.get(positions.get(position)), Moraledges_current);
			new_edge.addAll(remove_vertices(Moraledges_current, vertices_current.get(positions.get(position))));
			new_vert.addAll(thing(vertices_current, positions.get(position)));
			triangulate(new_edge, new_vert);
			return true;
		}
	}

	private ArrayList<Vertex<String>> thing(ArrayList<Vertex<String>> vertices_current, int position) {
		ArrayList<Vertex<String>> new_vert = new ArrayList<>();
		for(int i = 0; i < vertices_current.size(); i++)
			if(i != position)
			new_vert.add(vertices_current.get(i));
		return new_vert;
	}

	//Finds the number of new edges that would need to be added to triangulate at a given vertex
	private int[] num_new_edges(ArrayList<Edge<String>> edgeList, ArrayList<Vertex<String>> vertices_current){
		int[] result = new int[vertices_current.size()];
		Vertex<String> node;
		for(int m = 0; m < vertices_current.size();m++){
			node = vertices_current.get(m);
			int num_edges = 0;
			boolean in_list = false;
			ArrayList<Vertex<String>> neighbors = new ArrayList<>(3);
			for(int i = 0; i < edgeList.size(); i++)
				if(edgeList.get(i).getOrigin().equals(node))
					neighbors.add(edgeList.get(i).getDestination());
			for(int j = 0; j < neighbors.size(); j++)
				for(int k = j+1; k < neighbors.size(); k++){
					Edge<String> temp = new Edge<String>(neighbors.get(j),neighbors.get(k));
					in_list = false;
					for(int l = 0; l < edgeList.size(); l++){
						in_list |= edgeList.get(l).equals(temp);					
						if(in_list)
							break;
					}
					if(!in_list)
						num_edges++;
				}	
			result[m] = num_edges;
			num_edges = 0;
		}
			return result;
	}
	
	//Finds the weight, i.e. product of the number of states, of a neighborhood around a given vertex with a given set of edges.
	private int cluster_weight(Vertex<String> node, ArrayList<Edge<String>> edgeList){
		int weight = node.getNum_states();
		ArrayList<Vertex<String>> neighbors = new ArrayList<>(3);
		for(int i = 0; i < edgeList.size(); i++)
			if(edgeList.get(i).getOrigin().equals(node))
				neighbors.add(edgeList.get(i).getDestination());
		for(int j = 0; j < neighbors.size(); j++)
			weight *= neighbors.get(j).getNum_states();
		return weight;
	}

	//Finds if a given int array has an unigue minimum entry 
	private boolean unique_min(int [] array){
		boolean unique = true;
		int min = Integer.MAX_VALUE;
		int position = 0;
		for(int i = 0; i < array.length; i++){
			if(array[i] < min){
				position = i;
				min = array[i];	
			}						
		}
		for(int j = 0; j < array.length; j++){
			if(array[j] == min && position != j)
				unique = false;				
		}
		return unique;
	}

	//Finds the position of the minimum entry in an array of ints
	private int position_of_min(int[] array) {
		int min = Integer.MAX_VALUE;
		int position = 0;
		for(int i = 0; i < array.length; i++){
			if(array[i] < min){
				min = array[i];				
				position = i;
			}
		}
		return position;
	}

	//Returns the position in an int array of the min value, if more than one min value, returns the position of the first instance of the value.
	private ArrayList<Integer> positions_of_min(int [] array){
		ArrayList<Integer> results = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < array.length; i++){
			if(array[i] < min)
				min = array[i];				
		}
		for(int j = 0; j < array.length; j++){
			if(array[j] == min)
				results.add(j);				
		}
		return results;
	}
	
	//Given a vertex, makes a cluster around that vertex by connecting all of the vertex's neighbors with undirected edges
	private ArrayList<Edge<String>> make_clusters(Vertex<String> vertex, ArrayList<Edge<String>> Moraledges_current) {
		//System.out.println(vertex.getValue());
		ArrayList<Vertex<String>> neighbors = new ArrayList<>(3);
		for(int i = 0; i < Moraledges_current.size(); i++)
			if(Moraledges_current.get(i).getOrigin().equals(vertex))
			    if(!(neighbors.contains(Moraledges_current.get(i).getDestination())))
				neighbors.add(Moraledges_current.get(i).getDestination());
		for(int j = 0; j < neighbors.size(); j++)
			for(int k = j+1; k < neighbors.size(); k++){
				if(!Moraledge(neighbors.get(j),neighbors.get(k))){
				Edge<String> temp = new Edge<String>(neighbors.get(j),neighbors.get(k));
				Moraledges_current.add(temp);
				moraledges.add(temp);
				temp = new Edge<String>(neighbors.get(k),neighbors.get(j));
				Moraledges_current.add(temp);
				moraledges.add(temp);
				}
			}
		add_clique(neighbors, vertex);		
		return Moraledges_current;
	}
	
	//Takes the members of a clique and, if they are not a subset of a previously made clique
	private void add_clique(ArrayList<Vertex<String>> neighbors, Vertex<String> vertex) {
		if(cliques.isEmpty()){
			Clique<String> temp = new Clique<String>(neighbors);
			if(!(temp.isMember(vertex)))
			    temp.add_member(vertex);
			cliques.add(temp);
		}
		else{
			Clique<String> temp = new Clique<String>(neighbors);
			temp.add_member(vertex);
			Clique<String> temp1 = new Clique<String>(temp.getMembers());
			boolean is_subset = false;
			int i = 0;
			while(!is_subset && i < cliques.size())
				is_subset |= cliques.get(i++).contains_subset(temp);
			if(!is_subset)
				cliques.add(temp1);
		}		
	}

	//Removes any edges that are connected to a given vertex from a list of edges.
	private ArrayList<Edge<String>> remove_vertices(ArrayList<Edge<String>> temp_edge,Vertex<String> vertex) {
		ArrayList<Edge<String>> result = new ArrayList<>();
		for(int i = 0; i < temp_edge.size(); i++){
			if(!(temp_edge.get(i).getOrigin().equals(vertex)||temp_edge.get(i).getDestination().equals(vertex)))
				result.add(temp_edge.get(i));
		}
		return result;
	}
	
	//Takes the generated list of cliques and uses these to make a join tree
	private JoinTree makeJoinTree() {
		ArrayList<Sepset<String>> sepset_list = make_sepset_list();
		JoinTree result = new JoinTree(cliques, sepset_list,vertices, this);
		return result;
	}

	private ArrayList<Sepset<String>> make_sepset_list() {
		ArrayList<Sepset<String>> sepset_list = new ArrayList<>();
		ArrayList<Vertex<String>> temp;
		for(int i = 0; i < cliques.size(); i++)
			for(int j = i+1; j < cliques.size(); j++){
				temp = new ArrayList<>();
				temp.addAll(cliques.get(i).intersection(cliques.get(j)));
				Sepset<String> sepset = new Sepset<String>(temp, cliques.get(i), cliques.get(j));
				if(!temp.isEmpty())
					sepset_list.add(sepset);
				}
		return sepset_list;
	}
}
