package core;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int verticesCount;
    private final List<Edge> edges;
    private final double[][] adjacencyMatrix;

    public Graph(int verticesCount) {
        this.verticesCount = verticesCount;
        this.edges = new ArrayList<>();
        this.adjacencyMatrix = new double[verticesCount][verticesCount];
    }

    public void addEdge(int u, int v, double weight) {
        edges.add(new Edge(u, v, weight));
        adjacencyMatrix[u][v] = weight;
        adjacencyMatrix[v][u] = weight; // Graf jest nieskierowany
    }

    public int getVerticesCount() { return verticesCount; }
    public List<Edge> getEdges() { return edges; }
    public double getWeight(int u, int v) { return adjacencyMatrix[u][v]; }
}