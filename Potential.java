import java.util.ArrayList;

public class Potential<K> {

    public ArrayList<Vertex<K>> vertices = new ArrayList<>();
    double[] one_dimension;
    double[][] two_dimension;
    double[][][] three_dimension;

    public Potential() {

    }

    public Potential(double[] one_dimension, Vertex<K> value) {
        vertices.add(value);
        this.one_dimension = new double[value.getNum_states()];
        for (int i = 0; i < this.one_dimension.length; i++)
            this.one_dimension[i] = one_dimension[i];
    }

    public Potential(double[][] two_dimension, Vertex<K> value1, Vertex<K> value2) {
        vertices.add(value1);
        vertices.add(value2);
        this.two_dimension = new double[value1.getNum_states()][value2.getNum_states()];
        for (int i = 0; i < this.two_dimension.length; i++)
            for (int j = 0; j < this.two_dimension[0].length; j++)
                this.two_dimension[i][j] = two_dimension[i][j];
    }

    public Potential(double[][][] three_dimension, Vertex<K> value1, Vertex<K> value2,
            Vertex<K> value3) {
        vertices.add(value1);
        vertices.add(value2);
        vertices.add(value3);
        this.three_dimension = new double[value1.getNum_states()][value2.getNum_states()][value3
                .getNum_states()];
        for (int i = 0; i < this.three_dimension.length; i++)
            for (int j = 0; j < this.three_dimension[0].length; j++)
                for (int k = 0; k < this.three_dimension[0][0].length; k++)
                    this.three_dimension[i][j][k] = three_dimension[i][j][k];
    }

    // Multiplies the current potential by a given one.
    public Potential<K> multiply_potentials(Potential<K> given) {
        if (this.vertices.size() == 3) {
            if (given.vertices.size() == 1)
                return multiply1x3(given);
            if (given.vertices.size() == 2)
                return multiply2x3(given);
            if (given.vertices.size() == 3)
                return multiply3x3(given);
        }
        if (this.vertices.size() == 2) {
            if (given.vertices.size() == 1)
                return multiply1x2(given);
            if (given.vertices.size() == 2)
                return multiply2x2(given);
            if (given.vertices.size() == 3)
                return given.multiply2x3(this);
        }
        if(this.vertices.size() == 1){
            if (given.vertices.size() == 1)
                return multiply1x1(given);
            if (given.vertices.size() == 2)
                return given.multiply1x2(this);
            if (given.vertices.size() == 3)
                return given.multiply1x3(this);
        }
        return null;

    }

    private Potential<K> multiply1x1(Potential<K> given) {
        double[] result_array = new double[this.one_dimension.length];
        for(int i = 0; i < result_array.length; i++)
            result_array[i] = this.one_dimension[i]*given.one_dimension[i];
        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0));
        return result;
    }

    private Potential<K> multiply1x2(Potential<K> given) {
        double[][] result_array = new double[this.two_dimension.length][this.two_dimension[0].length];
        if (given.vertices.get(0).equals(this.vertices.get(0))) {
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    result_array[i][j] = this.two_dimension[i][j] * given.one_dimension[i];
        }
        else {
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    result_array[i][j] = this.two_dimension[i][j] * given.one_dimension[j];
        }
        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0),
                this.vertices.get(1));
        return result;
    }

    private Potential<K> multiply2x2(Potential<K> given) {
        double[][] result_array = new double[this.two_dimension.length][this.two_dimension[0].length];
        if (given.vertices.get(1).equals(this.vertices.get(1))) {
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    result_array[i][j] = this.two_dimension[i][j] * given.two_dimension[i][j];
        }
        else {
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    result_array[i][j] = this.two_dimension[i][j] * given.two_dimension[j][i];
        }
        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0),
                this.vertices.get(1));
        return result;
    }

    // Takes a 3 dimensional potential and finds where in a 3-d potential it
    // matches vertices and then multiplies the 3-d potential into the
    // 3-d one. If the 3-d one has vertices "ACE" the matches are shown below
    private Potential<K> multiply3x3(Potential<K> given) {
        double[][][] result_array = new double[this.three_dimension.length][this.three_dimension[0].length][this.three_dimension[0][0].length];
        if (given.vertices.get(0).equals(this.vertices.get(0))) {
            if (given.vertices.get(1).equals(this.vertices.get(1)))// "ACE"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[i][j][k];
            else
                // "AEC"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[i][k][j];
        }
        else if (given.vertices.get(0).equals(this.vertices.get(1))) {
            if (given.vertices.get(1).equals(this.vertices.get(0)))// "CAE"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[j][i][k];
            else
                // "CEA"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[j][k][i];
        }
        else {
            if (given.vertices.get(1).equals(this.vertices.get(0)))// "EAC"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[k][i][j];
            else
                // "ECA"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.three_dimension[k][j][i];
        }
        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0),
                this.vertices.get(1), this.vertices.get(2));
        return result;
    }

    // Takes a 2 dimensional potential and finds where in a 3-d potential it
    // matches vertices and then multiplies the 2-d potential into the
    // 3-d one. If the 3-d one has vertices "ACE" the matches are shown below
    private Potential<K> multiply2x3(Potential<K> given) {
        double[][][] result_array = new double[this.three_dimension.length][this.three_dimension[0].length][this.three_dimension[0][0].length];
        if (given.vertices.get(0).equals(this.vertices.get(0))) {
            if (given.vertices.get(1).equals(this.vertices.get(1)))// "AC"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[i][j];
            else
                // "AE"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[i][k];
        }
        else if (given.vertices.get(0).equals(this.vertices.get(1))) {
            if (given.vertices.get(1).equals(this.vertices.get(0)))// "CA"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[j][i];
            else
                // "CE"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[j][k];
        }
        else {
            if (given.vertices.get(1).equals(this.vertices.get(0)))// "EA"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[k][i];
            else
                // "EC"
                for (int i = 0; i < result_array.length; i++)
                    for (int j = 0; j < result_array[0].length; j++)
                        for (int k = 0; k < result_array[0][0].length; k++)
                            result_array[i][j][k] = this.three_dimension[i][j][k]
                                    * given.two_dimension[k][j];
        }
        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0),
                this.vertices.get(1), this.vertices.get(2));
        return result;
    }

    // Takes a 1 dimensional potential and finds where in a 3-d potential it
    // matches vertices and then multiplies the 1-d potential into the
    // 3-d one. If the 3-d one has vertices "ACE" the matches are shown below
    private Potential<K> multiply1x3(Potential<K> given) {
        double[][][] result_array = new double[this.three_dimension.length][this.three_dimension[0].length][this.three_dimension[0][0].length];
        if (given.vertices.get(0).equals(this.vertices.get(0))) {// "A"
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    for (int k = 0; k < result_array[0][0].length; k++)
                        result_array[i][j][k] = this.three_dimension[i][j][k]
                                * given.one_dimension[i];
        }
        else if (given.vertices.get(0).equals(this.vertices.get(1))) {// "C"
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    for (int k = 0; k < result_array[0][0].length; k++)
                        result_array[i][j][k] = this.three_dimension[i][j][k]
                                * given.one_dimension[j];
        }
        else
            // "E"
            for (int i = 0; i < result_array.length; i++)
                for (int j = 0; j < result_array[0].length; j++)
                    for (int k = 0; k < result_array[0][0].length; k++)
                        result_array[i][j][k] = this.three_dimension[i][j][k]
                                * given.one_dimension[k];

        Potential<K> result = new Potential<K>(result_array, this.vertices.get(0),
                this.vertices.get(1), this.vertices.get(2));
        return result;
    }

    // Sums over the given vertex removing it from the current potential
    public Potential<K> sum_out(Vertex<K> variable) {
        if (two_dimension != null)
            return sum_out2d(variable);
        return sum_out3d(variable);
    }

    // Given a potential with 3 dimensions, takes the sum over like combinations
    // of states for the variables not being summed over. This method
    // effectively removes the variable in question from the table while keeping
    // the information it stored in the table.
    private Potential<K> sum_out3d(Vertex<K> variable) {
        double[][] result_array;
        Potential<K> result = null;
        if (variable.equals(vertices.get(0))) {
            result_array = new double[three_dimension[0].length][three_dimension[0][0].length];
            for (int i = 0; i < three_dimension.length; i++)
                for (int j = 0; j < three_dimension[0].length; j++)
                    for (int k = 0; k < three_dimension[0][0].length; k++)
                        result_array[j][k] += three_dimension[i][j][k];
            result = new Potential<K>(result_array, vertices.get(1), vertices.get(2));
        }
        else if (variable.equals(vertices.get(1))) {
            result_array = new double[three_dimension.length][three_dimension[0][0].length];
            for (int i = 0; i < three_dimension.length; i++)
                for (int j = 0; j < three_dimension[0].length; j++)
                    for (int k = 0; k < three_dimension[0][0].length; k++)
                        result_array[i][k] += three_dimension[i][j][k];
            result = new Potential<K>(result_array, vertices.get(0), vertices.get(2));
        }
        else {
            result_array = new double[three_dimension.length][three_dimension[0].length];
            for (int i = 0; i < three_dimension.length; i++)
                for (int j = 0; j < three_dimension[0].length; j++)
                    for (int k = 0; k < three_dimension[0][0].length; k++)
                        result_array[i][j] += three_dimension[i][j][k];
            result = new Potential<K>(result_array, vertices.get(0), vertices.get(1));
        }

        return result;
    }

    // Given a potential with 3 dimensions, takes the sum over like combinations
    // of states for the variables not being summed over. This method
    // effectively removes the variable in question from the table while keeping
    // the information it stored in the table.
    private Potential<K> sum_out2d(Vertex<K> variable) {
        double[] result_array;
        Potential<K> result = null;
        if (variable.equals(vertices.get(0))) {
            result_array = new double[two_dimension[0].length];
            for (int i = 0; i < two_dimension.length; i++)
                for (int j = 0; j < two_dimension[0].length; j++)
                    result_array[j] += two_dimension[i][j];
            result = new Potential<K>(result_array, vertices.get(1));
        }
        else {
            result_array = new double[two_dimension.length];
            for (int i = 0; i < two_dimension.length; i++)
                for (int j = 0; j < two_dimension[0].length; j++)
                    result_array[i] += two_dimension[i][j];
            result = new Potential<K>(result_array, vertices.get(0));
        }
        return result;
    }

    // Returns the probabilities associated with the possible states of the
    // given variable. Sums out all other variables.
    public Potential<K> marginalize(Vertex<K> variable) {
        Potential<K> result = this;
        for (int i = 0; i < vertices.size(); i++)
            if (!(vertices.get(i).equals(variable)))
                result = result.sum_out(vertices.get(i));
        return result;
    }

    // Divides each element in the current potential by the corresponding
    // element in a given potential
    public Potential<K> divide(Potential<K> given) {
        if (vertices.size() == 1)
            return divide1x1(given);
        return divide2x2(given);
    }

    // Divides 2 2d potentials term-by-term
    private Potential<K> divide2x2(Potential<K> given) {
        double[][] result_array = new double[two_dimension.length][two_dimension[0].length];
        Potential<K> result = null;
        for (int i = 0; i < two_dimension.length; i++)
            for (int j = 0; j < two_dimension[0].length; j++)
                result_array[i][j] = two_dimension[i][j] / given.two_dimension[i][j];
        result = new Potential<K>(result_array, vertices.get(0), vertices.get(1));
        return result;
    }

    // Divides 2 1 potentials term-by-term
    private Potential<K> divide1x1(Potential<K> given) {
        double[] result_array = new double[one_dimension.length];
        Potential<K> result = null;
        for (int i = 0; i < one_dimension.length; i++)
            result_array[i] = one_dimension[i] / given.one_dimension[i];
        result = new Potential<K>(result_array, vertices.get(0));
        return result;
    }

    // Sets the sum of the potential to be 1, making given probabilities valid
    public Potential<K> normalize() {
        Potential<K> result = null;
        double sum = 0.0;
        if (one_dimension != null) {
            for (int i = 0; i < one_dimension.length; i++)
                sum += one_dimension[i];
            for (int i = 0; i < one_dimension.length; i++)
                one_dimension[i] /= sum;
            result = new Potential<K>(this.one_dimension, vertices.get(0));
            return result;
        }
        if (two_dimension != null) {
            for (int i = 0; i < two_dimension.length; i++)
                for (int j = 0; j < two_dimension[0].length; j++)
                    sum += two_dimension[i][j];
            for (int i = 0; i < two_dimension.length; i++)
                for (int j = 0; j < two_dimension[0].length; j++)
                    two_dimension[i][j] /= sum;
            result = new Potential<K>(this.two_dimension, vertices.get(0), vertices.get(1));
            return result;

        }
        for (int i = 0; i < three_dimension.length; i++)
            for (int j = 0; j < three_dimension[0].length; j++)
                for (int k = 0; k < three_dimension[0][0].length; k++)
                    sum += three_dimension[i][j][k];
        for (int i = 0; i < three_dimension.length; i++)
            for (int j = 0; j < three_dimension[0].length; j++)
                for (int k = 0; k < three_dimension[0][0].length; k++)
                    three_dimension[i][j][k] /= sum;
        result = new Potential<K>(this.three_dimension, vertices.get(0), vertices.get(1),
                vertices.get(2));
        return result;
    }

    // Returns the list of vertex values and the potential table.
    public String toString() {
        String result = "";
        for (int i = 0; i < vertices.size(); i++)
            result += vertices.get(i).getValue() + " ";
        result += "\n" + printtable();
        return result;
    }

    // Returns the values of the table as a string.
    private String printtable() {
        String result = "";
        if (one_dimension != null) {
            for (int i = 0; i < one_dimension.length; i++)
                result += one_dimension[i] + "\t";
            return result;
        }
        if (two_dimension != null) {
            for (int i = 0; i < two_dimension.length; i++) {
                for (int j = 0; j < two_dimension[0].length; j++)
                    result += two_dimension[i][j] + "\t";
                result += "\n";
            }
            return result;
        }
        if (three_dimension != null)
            for (int i = 0; i < three_dimension.length; i++) {
                for (int j = 0; j < three_dimension[0].length; j++) {
                    for (int k = 0; k < three_dimension[0][0].length; k++)
                        result += three_dimension[i][j][k] + "\t";
                    result += "\n";
                }
                result += "\n";
            }
        return result;
    }

    // Gives the one_dimension array as this is the result of the
    // marginalization
    public double[] getOne_dimension() {
        return one_dimension;
    }
}
