package com.lesson.ads.chapter14;

import com.lesson.ads.chapter14.EdgeWeightedDirectedGraph.DirectedEdge;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DijkstraShortestPathSearch {

  public static Optional<DijkstraShortestPathSearchResult> search(
      EdgeWeightedDirectedGraph graph, int startVertex, int finishVertex) {
    validateGraph(graph);
    validateVertex(graph, startVertex);
    validateVertex(graph, finishVertex);

    var nearestVertices = new PriorityQueue<MeteredVertex>();
    var distances = IntStream.range(0, graph.getVertexNum())
        .mapToDouble(any -> Double.POSITIVE_INFINITY).toArray();
    var edges = new DirectedEdge[graph.getVertexNum()];
    nearestVertices.add(new MeteredVertex(startVertex, 0D));
    distances[startVertex] = 0D;
    while (!nearestVertices.isEmpty()) {
      var fromVertex = nearestVertices.poll().vertex;
      if (fromVertex == finishVertex) {
        break;
      } else {
        graph.adjacentEdges(fromVertex).forEach(adjacentEdge -> {
          int toVertex = adjacentEdge.getToVertex();
          if (distances[toVertex] > distances[fromVertex] + adjacentEdge.getDistance()) {
            edges[toVertex] = adjacentEdge;
            distances[toVertex] = distances[fromVertex] + adjacentEdge.getDistance();
            var meteredVertex = new MeteredVertex(toVertex, distances[toVertex]);
            nearestVertices.remove(meteredVertex);
            nearestVertices.add(meteredVertex);
          }
        });
      }
    }
    return edges[finishVertex] == null ?
        Optional.empty() : Optional.of(path(finishVertex, edges, distances));
  }

  private static void validateGraph(EdgeWeightedDirectedGraph graph) {
    if (graph == null) {
      throw new IllegalArgumentException("Graph cannot be null");
    }
  }

  private static void validateVertex(EdgeWeightedDirectedGraph graph, int vertex) {
    if (vertex >= graph.getVertexNum()) {
      throw new IllegalArgumentException(String.format(
          "Vertex does not belong to the graph: numberOfVertex=%s, vertex=%s",
          graph.getVertexNum(), vertex));
    }
  }

  private static void validateHeuristic(Function<DirectedEdge, Double> heuristic) {
    if (heuristic == null) {
      throw new IllegalArgumentException("Heuristic function cannot be null");
    }
  }

  private static DijkstraShortestPathSearchResult path(
      int finishVertex, DirectedEdge[] edges, double[] distances) {
    var vertices = new LinkedList<Integer>();
    vertices.addFirst(finishVertex);
    var edge = edges[finishVertex];
    while (edge != null) {
      vertices.addFirst(edge.getFromVertex());
      edge = edges[edge.getFromVertex()];
    }

    var distance = distances[finishVertex];
    var numberOfVisitedEdges = Arrays.stream(edges).filter(Objects::nonNull).count();
    return new DijkstraShortestPathSearchResult(vertices, distance, numberOfVisitedEdges);
  }

  private record MeteredVertex(
      int vertex,
      double distance
  ) implements Comparable<MeteredVertex> {

    @Override
    public int hashCode() {
      return Integer.hashCode(vertex);
    }

    @Override
    public boolean equals(Object that) {
      return that != null &&
          that.getClass() == this.getClass() &&
          Objects.equals(((MeteredVertex) that).vertex, this.vertex);
    }

    @Override
    public int compareTo(MeteredVertex that) {
      return Double.compare(this.distance, that.distance);
    }
  }

  public record DijkstraShortestPathSearchResult(
      List<Integer> vertices,
      double distance,
      long numberOfVisitedEdges) {

  }
}