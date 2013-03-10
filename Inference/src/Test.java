
public class Test {

	public static void main(String[] args) {
		Bayes_net<String> sample = new Bayes_net<>();
		double[] a = {.5,.5};
		sample.add_vertex("A",2, a);
		sample.add_vertex("B",2);
		sample.add_vertex("C",2);
		sample.add_vertex("D",2);
		sample.add_vertex("E",2);
		sample.add_vertex("F",2);
		sample.add_vertex("G",2);
		sample.add_vertex("H",2);
		double[][] ab = {{.5,.5},{0.4,0.6}};
		double[][] ac = {{.7,.3},{0.2,0.8}};
		double[][] cg = {{.8,.2},{.1,.9}};
		double[][] ce = {{.3,.7},{.6,.4}};
		double[][][] geh = {{{.05,.95},{.95,.05}},{{.95,.05},{.95,.05}}};
		double[][] bd = {{.9,.1},{.5,.5}};
		double[][][] def = {{{.01,.99},{.01,.99}},{{.01,.99},{.99,.01}}};
		sample.add_edge("A",2, "B",2,ab);
		sample.add_edge("A",2, "C",2,ac);
		sample.add_edge("B",2,"D",2,bd);
		sample.add_edge("C",2,"E",2,ce);
		sample.add_edge("C",2,"G",2,cg);
		sample.add_edge("G",2,"H",2);
		sample.add_edge("E",2,"H",2,geh);
		sample.add_edge("E",2,"F",2);
		sample.add_edge("D",2,"F",2,def);
		JoinTree<String> test = sample.convertToJoinTree();
		System.out.println(test.makeConsistent());
		double [] result = test.find_all_probabilities();
		for(int i = 0; i < result.length; i++)
			System.out.println(result[i]);
	}

}
