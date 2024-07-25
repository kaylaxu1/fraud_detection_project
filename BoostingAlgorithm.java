import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Arrays;
import java.util.LinkedList;

public class BoostingAlgorithm {
    // use a linked list to store all of the WeakLearner models
    // train each model with the modified weights (where misclassified
    // weights are made larger)

    private double[] weights; // weight array
    private double[][] input; // transactions
    private int[] labels; // labels of data
    private Clustering clusterLocations; // clustering object
    // stores weak learners of all iterations
    private LinkedList<WeakLearner> list;

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(double[][] input, int[] labels,
                             Point2D[] locations, int k) {
        // exceptions
        if (labels == null || input == null || locations == null)
            throw new IllegalArgumentException("Arrays cannot be null!");

        int m = locations.length;
        if (k < 1 || k > m)
            throw new IllegalArgumentException("k is an invalid size");

        int n = input.length;
        list = new LinkedList<WeakLearner>();

        // protect against next check
        if (input[0] == null)
            throw new IllegalArgumentException("Null first transaction");

        // check if lengths are consistent
        if (!(n == labels.length && input[0].length == locations.length))
            throw new IllegalArgumentException("Inconsistent Lengths");

        double[][] newInput = new double[n][k];
        // create new clustering object
        clusterLocations = new Clustering(locations, k);
        for (int i = 0; i < n; i++) {
            // check for null arrays
            if (input[i] == null)
                throw new IllegalArgumentException("Input cannot be null!");

            // reduce dimensions for each transaction
            newInput[i] = clusterLocations.reduceDimensions(input[i]);
        }
        weights = new double[n];
        for (int w = 0; w < weights.length; w++) {
            weights[w] = (1.0 / n);
        }
        this.input = newInput; // store dimension-reduced input as new input
        this.labels = Arrays.copyOf(labels, labels.length);

    }

    // return the current weights
    public double[] weights() {
        return Arrays.copyOf(weights, weights.length);
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        WeakLearner weakLearner = new WeakLearner(input,
                                                  weights, labels);
        double weightSum = 0;
        for (int i = 0; i < input.length; i++) {
            // make prediction for each transaction
            int prediction = weakLearner.predict(input[i]);
            if (prediction != labels[i]) {
                weights[i] *= 2;
            }
            weightSum += weights[i];
        }

        // renormalize weights
        for (int j = 0; j < weights.length; j++) {
            weights[j] /= weightSum;
        }

        // adding the updated WeakLearner model to the linked list
        list.addLast(weakLearner);
    }

    // return the prediction of the learner for a new sample
    public int predict(double[] sample) {

        double[] newSample = clusterLocations.reduceDimensions(sample);

        // sum of predictions of 1
        int oneCount = 0;
        int zeroCount = 0;

        // looking at outputs of all the weak learners
        for (WeakLearner item : list) {
            // adding the prediction (0 or 1) to the running sum
            if (item.predict(newSample) == 1)
                oneCount++;
            else zeroCount++;
        }
        // checking for majority vote to see if there are more 1s or 0s
        if (oneCount > zeroCount) return 1;
        else return 0;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet test = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int iterations = Integer.parseInt(args[3]);

        Stopwatch stopwatch = new Stopwatch();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(training.input,
                                                        training.labels,
                                                        training.locations, k);

        double[] weights = model.weights();
        // print first ten weights
        for (int w = 0; w < 10; w++)
            StdOut.println("Weights: " + weights[w]);

        for (int t = 0; t < iterations; t++)
            model.iterate();

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

        StdOut.println("Stopwatch time: " + stopwatch.elapsedTime());

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model:     " + testAccuracy);
    }
}
