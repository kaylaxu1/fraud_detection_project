We created an edge-weighted graph with paths between all pairs of locations and
weighted by the distance between them. We then find a minimum spanning tree on
this graph, and take m-k lightest edges. Then, we find the remaining connected
components, which define our clusters. We used the functions from the given APIs
to do this.

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
acy)

