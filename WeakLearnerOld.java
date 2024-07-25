import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class WeakLearnerOld {
    private int k; // number of dimensions
    private int bestDim; // best dimension of comparison
    private double bestValue; // best line of comparison
    private int bestSign; // best 0/1 separator

    // for sorting, there are 2 ways
    // 1: create another comparable class (Training Instances) that stores
    // (coordinates, labels, weights) and compares by weight and calling
    // arrays.sort on TI to sort by weight
    // 2: calling merge.indices.sort (which maps indices to sorted values, where
    // the new values are the indices themselves but in sorted order by weight)
    // Compares the two terms in descending order by weight.
    /*
    create a class with two instance variables (for each element at the same position
     of the array)
     then, implement Comparable by writing down a compareTo() method that
     compares by coordinate.
     When you run Arrays.sort() on this array of tuples, you can access
     the coordinates in increasing order along with their associated labels.
     */

    public class Record implements Comparable<Record> {
        private int dim; // dimension of comparison
        private int[] record;

        public Record(int[] record, int k) {
            dim = k;
            this.record = Arrays.copyOf(record, record.length);
        }

        // Compares the two terms in lexicographic order by query.
        public int compareTo(Record other) {
            int myCoord = record[dim];
            int otherCoord = other.record[dim];
            if (myCoord > otherCoord) return 1;
            else if (myCoord < otherCoord) return -1;
            else return 0;
        }
    }


    // train the weak learner
    public WeakLearnerOld(double[][] input, double[] weights, int[] labels) {
        // every sign, every dim, every value (sum up correct weights)
        // sweep line over each dimension
        // accumulate weights of label on either side of split, at each step (calculate sum of correctly calculated weights and keep track of the best))
        // -keep running sum (update weights: add or subtract value of the point that the new line crosses)

        // private helper method: sort input by values in each dimension; auxillary class w/values, indices, customCompareTo

        // do we have to check each element in array for nulls???
        if (weights == null || labels == null || input == null)
            throw new IllegalArgumentException("Arrays cannot be null!");

        k = input[0].length;
        int n = input.length;
        // check if lengths are consistent
        if (n != weights.length || n != labels.length)
            throw new IllegalArgumentException("Lengths are not consistent!");

        for (int w = 0; w < weights.length; w++) {
            if (weights[w] < 0) {
                throw new IllegalArgumentException("Weights are negative!");
            }
        }

        for (int v = 0; v < labels.length; v++) {
            if (labels[v] != 0 && labels[v] != 1) {
                throw new IllegalArgumentException("Invalid labels!");
            }
        }

        double bestWeight = 0;
        double bestValue = Double.POSITIVE_INFINITY;
        int bestDim = Integer.MAX_VALUE;
        int bestSign = Integer.MAX_VALUE;

        // loop through all dimensions
        for (int dim = 0; dim < k; dim++) {
            for (int sign = 0; sign < 2; sign++) {
                for (int i = 0; i < input.length; i++) {
                    // looks at all possible dimension values (of every point)

                    // check for each record being null in input
                    if (input[i] == null)
                        throw new IllegalArgumentException("Arguments cannot "
                                                                   + "be null!");
                    double vP = input[i][dim];
                    double currentWeight = 0;

                    // checks for the prediction on all the other points
                    for (int j = 0; j < input.length; j++) {
                        int predLabel = 0;
                        if (input[j][dim] == vP)
                            predLabel = sign; // 0 if sign = 0, 1 if sign = 1

                        else if (input[j][dim] < vP) {
                            if (sign == 1)
                                predLabel = 1;
                        }
                        else if (sign == 0)
                            predLabel = 1;

                        // if predicted label is 1 and differs from actual label,
                        // update correct weights
                        if (predLabel == labels[j]) {
                            currentWeight += weights[j];
                        }
                    }
                    if (currentWeight > bestWeight) {
                        bestWeight = currentWeight;
                        bestDim = dim;
                        bestValue = vP;
                        bestSign = sign;
                    }
                }
            }
        }

        this.bestDim = bestDim;
        this.bestValue = bestValue;
        this.bestSign = bestSign;

    }

    // return the prediction of the learner for a new sample
    public int predict(double[] sample) {
        if (sample == null)
            throw new IllegalArgumentException("Input cannot be null!");
        if (sample.length != k)
            throw new IllegalArgumentException("Sample length is invalid");

        if (sample[bestDim] <= bestValue) {
            if (bestSign == 0)
                return 0;
        }
        else if (bestSign == 1)
            return 0;
        return 1;
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return bestDim;
    }

    // return the value the learner uses to separate the data
    public double valuePredictor() {
        return bestValue;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return bestSign;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet test = new DataSet(args[1]);
        // int k = Integer.parseInt(args[2]);

        // set uniform weights of 1
        double[] weights = new double[training.labels.length];
        for (int t = 0; t < training.labels.length; t++)
            weights[t] = 1;

        // train the model
        WeakLearnerOld model = new WeakLearnerOld(training.input,
                                                  weights, training.labels);
        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.n; i++)
            if (model.predict(training.input[i]) == training.labels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.n;

        // calculate the test data set accuracy
        double testAccuracy = 0;
        for (int i = 0; i < test.n; i++)
            if (model.predict(test.input[i]) == test.labels[i])
                testAccuracy += 1;
        testAccuracy /= test.n;

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model:     " + testAccuracy);

    }
}
