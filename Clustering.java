import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Clustering {
    private CC clusters; // clusters of locations
    private int m; // number of locations

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {

        if (locations == null)
            throw new IllegalArgumentException("Input array cannot be null");

        m = locations.length;

        if (k < 1 || k > m)
            throw new IllegalArgumentException("k is an invalid length");

        EdgeWeightedGraph weightedGraph = new EdgeWeightedGraph(m);
        // add edge between every pair of vertices,
        // weighted by Euclidean distance
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                if (locations[i] == null || locations[j] == null)
                    throw new IllegalArgumentException("Arguments cannot be "
                                                               + "null");
                double distance = locations[i].distanceTo(locations[j]);
                weightedGraph.addEdge(new Edge(i, j, distance));
            }
        }

        // find minimum spanning tree
        KruskalMST mst = new KruskalMST(weightedGraph);
        Iterable<Edge> mstEdges = mst.edges();

        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(m);
        int counter = 0;
        // only consider m-k edges of least weight
        for (Edge e : mstEdges) {
            if (counter < m - k)
                clusterGraph.addEdge(e);
            counter++;
        }

        // find connected components in cluster graph
        clusters = new CC(clusterGraph);

    }

    // return the cluster of the ith point
    public int clusterOf(int i) {
        if (i < 0 || i > m - 1)
            throw new IllegalArgumentException("k is an invalid length");

        return clusters.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public double[] reduceDimensions(double[] input) {

        if (input == null) {
            throw new IllegalArgumentException("input array cannot be null");
        }

        if (input.length != m)
            throw new IllegalArgumentException("Length is invalid");

        double[] reduced = new double[clusters.count()];
        for (int i = 0; i < input.length; i++) {
            reduced[clusters.id(i)] += input[i];
        }
        return reduced;
    }

    // unit testing (required)
    public static void main(String[] args) {
        Point2D[] locations = new Point2D[] {
                new Point2D(1, 2), new Point2D(1, 3),
                new Point2D(4, 2),
                new Point2D(3, 5), new Point2D(6, 7)
        };

        Clustering clusterLocations = new Clustering(locations, 2);
        StdOut.print(clusterLocations.clusterOf(2));
        StdOut.print(clusterLocations.clusterOf(4));

        double[] forReduction = new double[] { 1, 3, 4, 0, 2 };

        double[] reducedDim = clusterLocations.reduceDimensions(forReduction);
        for (int j = 0; j < reducedDim.length; j++) {
            StdOut.println(reducedDim[j]);
        }

        // read file from stdin and create array of locations
        Point2D[] points = new Point2D[StdIn.readInt()];
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            points[i] = new Point2D(x, y);
            i++;
        }
        // find clusters
        Clustering clusterLocations2 = new Clustering(points, 2);
        StdOut.println(clusterLocations2.clusterOf(1));


    }
}
