import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class WeakLearner {
    private int k; // number of dimensions
    private int bestDim; // best dimension of comparison
    private double bestValue; // best line of comparison
    private int bestSign; // best 0/1 separator

    // for sorting the transactions by dimension value
    private class Transaction implements Comparable<Transaction> {
        private int dim; // dimension of comparison
        private double[] transaction; // one transaction record
        private int index; // index in the original unsorted array

        // store transaction and dimension of comparison
        public Transaction(double[] transaction, int dim, int index) {
            this.dim = dim;
            this.transaction = transaction;
            this.index = index;
        }

        // Compares the two terms in lexicographic order by query.
        public int compareTo(Transaction other) {
            double myCoord = transaction[dim];
            double otherCoord = other.transaction[dim];
            return Double.compare(myCoord, otherCoord);
        }
    }

    // train the weak learner
    public WeakLearner(double[][] input, double[] weights, int[] labels) {
        if (weights == null || labels == null || input == null)
            throw new IllegalArgumentException("Arrays cannot be null!");

        int n = input.length;
        // check if lengths are consistent
        if (!(n == weights.length && weights.length == labels.length))
            throw new IllegalArgumentException("Lengths are not consistent!");

        // get number of dimensions
        if (input[0] == null)
            throw new IllegalArgumentException("Null input");
        k = input[0].length;

        // check for all nonnegative weights
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

        double optimalValue = Double.POSITIVE_INFINITY;
        double optimalWeight = 0;
        int optimalDim = Integer.MAX_VALUE;
        int optimalSign = Integer.MAX_VALUE;

        // loop through all dimensions
        for (int dim = 0; dim < k; dim++) {
            Transaction[] transactions = new Transaction[input.length];

            // turn inputs into transactions
            for (int i = 0; i < input.length; i++) {
                // check for each transaction being null in input
                if (input[i] == null)
                    throw new IllegalArgumentException("Arguments cannot "
                                                               + "be " + "null!");
                transactions[i] = new Transaction(input[i], dim, i);
            }

            // sort transactions and get first value
            Arrays.sort(transactions);

            Transaction startingTransaction = transactions[0];
            double startingDivider = startingTransaction.transaction[dim];

            //** sweep line algorithm to find parameters with best total weight **

            for (int sign = 0; sign < 2; sign++) {
                // find starting sum of weights
                double currentWeight = 0;
                for (int i = 0; i < input.length; i++) {
                    double val = input[i][dim];

                    int predLabel = 0;
                    // get initial weight sum of first divider
                    if (val == startingDivider)
                        predLabel = sign; // 0 if sign = 0, 1 if sign = 1
                    else if (sign == 0) // check points above
                        predLabel = 1;

                    // update weight
                    if (predLabel == labels[i])
                        currentWeight += weights[i];
                }
                double startingWeight = currentWeight;

                if (startingWeight > optimalWeight) {
                    optimalWeight = startingWeight;
                    optimalDim = dim;
                    optimalValue = startingDivider;
                    optimalSign = sign;
                }

                double divider = startingDivider;

                // sweep line and update parameters if greater weight is found
                for (int j = 1; j < transactions.length; j++) {
                    Transaction a = transactions[j];

                    if (a.transaction[dim] == startingDivider) {
                        continue;
                    }
                    double div = a.transaction[dim];
                    // if new point on dividing line is now correctly classified
                    if (labels[a.index] == sign)
                        currentWeight += weights[a.index];
                    else  // if new point on dividing line is misclassified
                        currentWeight -= weights[a.index];

                    if (currentWeight > startingWeight) {
                        if (j != transactions.length - 1) {
                            Transaction next = transactions[j + 1];
                            if (a.transaction[dim] == next.transaction[dim]) {
                                continue;
                            }
                        }
                        startingWeight = currentWeight;
                        divider = div;
                    }

                    // update optimal global parameters
                    if (startingWeight > optimalWeight) {
                        optimalWeight = startingWeight;
                        optimalDim = dim;
                        optimalValue = divider;
                        optimalSign = sign;
                    }
                }

            }
        }
        this.bestDim = optimalDim;
        this.bestValue = optimalValue;
        this.bestSign = optimalSign;
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

        // set uniform weights of 1
        double[] weights = new double[training.labels.length];
        for (int t = 0; t < training.labels.length; t++)
            weights[t] = 1;

        // train the model
        WeakLearner model = new WeakLearner(training.input,
                                            weights, training.labels);

        // print optimal parameters
        StdOut.println("Best Dimension: " + model.dimensionPredictor());
        StdOut.println("Best Value: " + model.valuePredictor());
        StdOut.println("Best Sign: " + model.signPredictor());

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
