Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */
We created an edge-weighted graph with paths between all pairs of locations and
weighted by the distance between them. We then find a minimum spanning tree on
this graph, and take m-k lightest edges. Then, we find the remaining connected
components, which define our clusters. We used the functions from the given APIs
to do this.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */

We created a private class Transaction to sort the transactions by the elements
of the dimension we are checking, and keep track of the index of the
transaction in the original array. Then, we implemented a sweep line algorithm
by sorting the array of transactions by a dimension within a for loop
going over all dimensions, finding the baseline weight when the dividing line
is on the point with the smallest value in that dimension, then
increasing/decreasing the weight if moving the line up to the next highest point
resulted in more point(s) being classified correctly/misclassified, respectively.
We then update a local variable (if the new weight is greater) to keep track
of the best running weight, sign, dividing value, and dimension so far.
We then updated the global best parameters at the end to construct the optimal
weak learner.

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the training.txt and test.txt datasets instead, otherwise
 *  this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
     5          100             0.841           0.683
     10         100             0.95            1.059
     40         100             0.969           2.572
     40         200             0.973           4.18
     60         200             0.976           6.1
     65         200             0.981           6.531
     70         200             0.974          7.304
     80         200             0.969          7.683
     160        200             0.931          11.836

    // changing T after finding best k around 60
    65        210             0.981          6.121
    65        220             0.98           7.191
    65        250             0.978          8.263
    65        300             0.977          9.546
    65        350             0.977          10.655

/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */
We found that the best values of k and T were a k = 65 and T of 210, which
gave us an accuracy of 0.981.
1. Our strategy was to hold T constant to find the best value of k (finding when the
accuracy started going down to approximate what k might be at the peak accuracy).
We then held the best value of k that we found to find the best value of T (by
increasing the amount of iterations and finding T at the peak test accuracy).

2. A small value of T leads to low test accuracy because it means the algorithm
does not go through many iterations.
Each iteration updates weights and in turn updates the value, sign and dimension
predictor by them, which means that the model becomes more optimal for predicting
correct values given true labels (aka maximizing prediction accuracy). Therefore,
if we don't have that many iterations, our prediction accuracy will be low on the
test dataset (since the model is not trained well enough to classify the locations
and therefore will not perform well on the test data).

3. A small k value means that we have very few clusters. Less clusters means
that points that are not very close together might be put into the same cluster,
so the clusters might not be very representative of the groupings of the
original locations, leading to inaccurate predictions on the test dataset.
Thus, we will have a low test accuracy.

Similarly, a large k value is too many clusters, which leads to overfitting on
the training data. The clusters will match the training data too well and
therefore will not perform as well on the different test data, leading to low test
accuracy.

/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
n/a

/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */
n/a

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
The assignment specs were unclear about corner cases like if there are two
points on a line and how to update the champion in that case (we would've
appreciated more hints). Explicit hints about sorting would have also been very
helpful. The information on which exceptions to throw were also not very
descriptive, leading to confusion on how to check length consistencies :(
