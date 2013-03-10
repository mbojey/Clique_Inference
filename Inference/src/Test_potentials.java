
public class Test_potentials {

	public static void main(String[] args) {
		double[][] second_array = {{0.7f, 0.3f},{0.2f,0.8f}};
		double[][] third_array = {{0.3f, 0.7f},{0.6f,0.4f}};
		double[][][] combined_array = {{{1,1},{1,1}},{{1,1},{1,1}}};
		Vertex<String> first_vertex = new Vertex<String>("A", 2);
		Vertex<String> second_vertex = new Vertex<String>("C", 2);
		Vertex<String> third_vertex = new Vertex<String>("E", 2);
		Potential <String> second = new Potential<String>(third_array, second_vertex, third_vertex);
		Potential <String> third = new Potential<String>(second_array, first_vertex, second_vertex);
		
		Potential<String> result = new Potential<String>(combined_array, first_vertex, second_vertex, third_vertex);
		System.out.print(result);
		result = result.multiply_potentials(second);
		result = result.multiply_potentials(third);
		System.out.print(result);
		result = result.sum_out(first_vertex);
		System.out.print(result);
		result = result.divide(second);
		System.out.print(result);
		/*result = result.sum_out(first_vertex);
		System.out.print(result);
		result = result.sum_out(second_vertex);
		System.out.print(result);
		result = new Potential<String>(combined_array, first_vertex, second_vertex, third_vertex);
		result = result.multiply_potentials(second);
		result = result.multiply_potentials(third);
		result = result.marginalize(third_vertex);
		System.out.print(result);*/
	}

}
