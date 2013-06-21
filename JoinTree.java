import java.util.ArrayList;

public class JoinTree {

    private ArrayList<Sepset<String>> sepsets = new ArrayList<>(1);
    private ArrayList<Clique<String>> cliques = new ArrayList<>(1);
    ArrayList<Vertex<String>> vertices = new ArrayList<>(1);
    private int total_states = 0;
    private Bayes_net bayesNet;

    // Constructor, builds a join tree from a list of cliques and a list of
    // sepsets
    public JoinTree(ArrayList<Clique<String>> cliques, ArrayList<Sepset<String>> sepsets,
            ArrayList<Vertex<String>> vertices, Bayes_net input) {
        this.bayesNet = input;
        this.vertices.addAll(vertices);
        for (int i = 0; i < vertices.size(); i++)
            total_states += vertices.get(i).getNum_states();
        Sepset<String> temp = new Sepset<String>();
        for (int i = 0; i < cliques.size() - 1; i++) {
            if (sepsets.isEmpty())
                break;
            if (unique_max_mass(sepsets)) {
                temp = find_max_mass(sepsets);
                if (!(this.cliques.contains(temp.getOrigin()) && this.cliques.contains(temp
                        .getDestination()))) {
                    if (!(this.cliques.contains(temp.getOrigin())))
                        this.cliques.add(temp.getOrigin());
                    if (!(this.cliques.contains(temp.getDestination())))
                        this.cliques.add(temp.getDestination());
                } else
                    i--;
                this.sepsets.add(temp);
                sepsets.remove(temp);
            } else {
                temp = find_min_cost(sepsets);
                if (!(this.cliques.contains(temp.getOrigin()) && this.cliques.contains(temp
                        .getDestination()))) {
                    if (!(this.cliques.contains(temp.getOrigin())))
                        this.cliques.add(temp.getOrigin());
                    if (!(this.cliques.contains(temp.getDestination())))
                        this.cliques.add(temp.getDestination());
                } else
                    i--;
                this.sepsets.add(temp);
                sepsets.remove(temp);
            }
        }
        cliques.trimToSize();
    }

    // Takes the join tree and makes it consistent by initializing and
    // propagating the belief tables properly??
    public boolean makeConsistent() {
        return initialize() && propagate();

    }

    // Finds the max mass in a list of sepsets and telle user if the max is
    // unique.
    private boolean unique_max_mass(ArrayList<Sepset<String>> sepsets) {
        int max = 0;
        int position = 0;
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).getMass() > max) {
                max = sepsets.get(i).getMass();
                position = i;
            }
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).getMass() == max && i != position)
                return false;
        return true;
    }

    // Finds the sepset from a list of sepsets that has the highest mass
    private Sepset<String> find_max_mass(ArrayList<Sepset<String>> sepsets) {
        int max = 0;
        Sepset<String> result = new Sepset<String>();
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).getMass() > max) {
                max = sepsets.get(i).getMass();
                result = sepsets.get(i);
            }
        return result;
    }

    // Finds the sepset from a list of sepsets that has the lowest cost
    private Sepset<String> find_min_cost(ArrayList<Sepset<String>> sepsets) {
        int min = Integer.MAX_VALUE;
        Sepset<String> result = new Sepset<String>();
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).getCost() < min) {
                min = sepsets.get(i).getCost();
                result = sepsets.get(i);
            }
        return result;
    }

    // Fills phi_x for all cliques in the join tree and sets phi_x for all
    // sepsets to 1. This method seems to not work, but maybe I am just
    // not understanding properly
    private boolean initialize() {
        setPhi();
        for (int k = 0; k < vertices.size(); k++)
            fillFamily(vertices.get(k));
        return true;
    }

    // Does the actual filling of the phi_x for the cliques and sepsets to 1
    private void setPhi() {
        for (int i = 0; i < cliques.size(); i++)
            cliques.get(i).initializePhi_x();
        for (int j = 0; j < sepsets.size(); j++)
            sepsets.get(j).initializePhi_x();
    }

    // Takes each vertex and changes phi_x for it's families clique.
    private void fillFamily(Vertex<String> vertex) {
        vertex.initialize_likelihood();
        Clique<String> temp = new Clique<String>(vertex.parents);
        temp.add_member(vertex);
        temp.setPhi_x(vertex.probabilities);
        int i = 0;
        while (!cliques.get(i).contains_subset(temp))
            i++;
        cliques.get(i).setPhi_x(cliques.get(i).getPhi_x().multiply_potentials(temp.getPhi_x()));

    }

    // Method called by the user to do global propagation
    private boolean propagate() {
        falsify();
        collect_evidence(cliques.get(0));
        falsify();
        distribute_evidence(cliques.get(0));
        normalize();
        return true;
    }

    // Sets all clique marks to false
    private void falsify() {
        for (int i = 0; i < cliques.size(); i++)
            cliques.get(i).setMark(false);
    }

    // Collects evidence through a series of recursive message passes.a Calls
    // collect evidence on any unmarked neighbors
    private void collect_evidence(Clique<String> clique) {
        clique.setMark(true);
        ArrayList<Clique<String>> neighbors = neighbors(clique);
        for (int i = 0; i < neighbors.size(); i++)
            if (!neighbors.get(i).isMark())
                collect_evidence(cliques.get(find_in_cliques(neighbors.get(i))),
                        cliques.get(find_in_cliques(clique)));

    }

    // Calls collect evidence on any unmarked neighbors and then passes a
    // message from the current clique to the clique which called the method
    // in the first place
    private void collect_evidence(Clique<String> new_clique, Clique<String> caller_clique) {
        new_clique.setMark(true);
        ArrayList<Clique<String>> neighbors = neighbors(new_clique);
        for (int i = 0; i < neighbors.size(); i++)
            if (!neighbors.get(i).isMark())
                collect_evidence(cliques.get(find_in_cliques(neighbors.get(i))),
                        cliques.get(find_in_cliques(new_clique)));
        message_pass(new_clique, caller_clique);

    }

    // Distributes evidence through a series of recursive message passes
    private void distribute_evidence(Clique<String> clique) {
        clique.setMark(true);
        ArrayList<Clique<String>> neighbors = neighbors(clique);
        for (int i = 0; i < neighbors.size(); i++)
            if (!neighbors.get(i).isMark()) {
                message_pass(clique, cliques.get(find_in_cliques(neighbors.get(i))));
                distribute_evidence(cliques.get(find_in_cliques(neighbors.get(i))));
            }
    }

    // Method that does the message passing, don't know how to do this yet,
    // maybe soon
    private void message_pass(Clique<String> passer, Clique<String> reciever) {
        Sepset<String> connection = find_sepset(passer, reciever);
        Potential<String> old_potential = connection.getPhi_x();
        Potential<String> new_potential = passer.getPhi_x();
        ArrayList<Vertex<String>> temp_list = passer.not_in_both(connection);
        for (int i = 0; i < temp_list.size(); i++)
            new_potential = new_potential.sum_out(temp_list.get(i));
        sepsets.get(find_in_sepsets(connection)).setPhi_x(new_potential);
        Potential<String> ratio = new_potential;
        ratio = ratio.divide(old_potential);
        cliques.get(find_in_cliques(reciever)).setPhi_x(
                cliques.get(find_in_cliques(reciever)).getPhi_x().multiply_potentials(ratio));
    }

    private int find_in_sepsets(Sepset<String> sepset) {
        int position = 0;
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).equals(sepset)) {
                position = i;
                break;
            }
        return position;
    }

    // Given two cliques, known to be connected by a sepset in 'sepsets' finds
    // that sepset;
    private Sepset<String> find_sepset(Clique<String> passer, Clique<String> reciever) {
        for (int i = 0; i < sepsets.size(); i++) {
            if ((sepsets.get(i).getOrigin().equals(passer) && sepsets.get(i).getDestination()
                    .equals(reciever))
                    ||
                    (sepsets.get(i).getOrigin().equals(reciever) && sepsets.get(i).getDestination()
                            .equals(passer)))
                return sepsets.get(i);
        }
        return null;

    }

    // Finds a given clique in the list of cliques
    private int find_in_cliques(Clique<String> clique) {
        int position = 0;
        for (int i = 0; i < cliques.size(); i++)
            if (cliques.get(i).equals(clique)) {
                position = i;
                break;
            }
        return position;
    }

    // Finds all cliques connected to a given clique using sepsets
    private ArrayList<Clique<String>> neighbors(Clique<String> clique) {
        ArrayList<Clique<String>> neighbors = new ArrayList<>(1);
        for (int i = 0; i < sepsets.size(); i++)
            if (sepsets.get(i).getOrigin().equals(clique))
                neighbors.add(sepsets.get(i).getDestination());
            else if (sepsets.get(i).getDestination().equals(clique))
                neighbors.add(sepsets.get(i).getOrigin());
        return neighbors;
    }

    // Normalizes cliques and sepsets making sure that the total probability
    // always adds to one.
    private boolean normalize() {
        return normalize_cliques() && normalize_sepsets();
    }

    private boolean normalize_sepsets() {
        for (int s = 0; s < sepsets.size(); s++)
            sepsets.get(s).setPhi_x(sepsets.get(s).getPhi_x().normalize());
        return true;
    }

    private boolean normalize_cliques() {
        for (int c = 0; c < cliques.size(); c++)
            cliques.get(c).setPhi_x(cliques.get(c).getPhi_x().normalize());
        return true;
    }

    // Returns the probability that a given value is in it's states. This is the
    // whole point of the thing.
    public double[] find_probability(Vertex<String> vertex) {
        double[] result = new double[vertex.getNum_states()];
        for (int i = 0; i < cliques.size(); i++) {
            if (cliques.get(i).find_position(vertex) != -1)
                result = cliques.get(i).getPhi_x().marginalize(vertex).getOne_dimension();
        }
        return result;
    }

    // Returns the probability that any value is in any state
    public double[] find_all_probabilities() {
        double[] result = new double[total_states];
        int counter = 0;
        double[] temp;
        for (int i = 0; i < vertices.size(); i++) {
            temp = find_probability(vertices.get(i));
            for (int j = 0; j < temp.length; j++)
                result[counter++] = temp[j];
        }
        return result;
    }

    // Enters evidence by being given a variable and the state that it is in
    public void enter_evidence(Vertex<String> variable, int state) {
        vertices.get(find_in_vertices(variable)).set_likelihood(state);
        setPhi();
        for (int k = 0; k < vertices.size(); k++)
            update_family(vertices.get(k));
        propagate();
    }

    private int find_in_vertices(Vertex<String> vertex) {
        int position = 0;
        for (int i = 0; i < vertices.size(); i++)
            if (vertices.get(i).equals(vertex)) {
                position = i;
                break;
            }
        return position;
    }

    private void update_family(Vertex<String> vertex) {
        Clique<String> temp = new Clique<String>(vertex.parents);
        temp.add_member(vertex);
        temp.setPhi_x(vertex.probabilities);
        int i = 0;
        while (!cliques.get(i).contains_subset(temp))
            i++;
        cliques.get(i).setPhi_x(cliques.get(i).getPhi_x().multiply_potentials(temp.getPhi_x()));
        cliques.get(i).setPhi_x(cliques.get(i).getPhi_x().multiply_potentials(vertex.likelihood));
    }

    public void Roll_over(Vertex<String> t_1vertex, Vertex<String> tvertex) {
        double[] current_belief = find_probability(tvertex);
        bayesNet.vertices.get(bayesNet.findVertex(t_1vertex.getValue(), t_1vertex.getNum_states())).setprobabilities(current_belief);
        JoinTree newone = bayesNet.convertToJoinTree();
        newone.makeConsistent();
        this.sepsets = newone.sepsets;
        this.cliques = newone.cliques;
        this.vertices = newone.vertices;
        this.bayesNet = newone.bayesNet;
    }
}
