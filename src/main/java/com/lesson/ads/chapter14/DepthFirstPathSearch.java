package com.lesson.ads.chapter14;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DepthFirstPathSearch {

  public static Optional<DepthFirstPathSearchResult> search(
      Graph graph, int startVertex, int finishVertex) {
    validateGraph(graph);
    validateVertex(graph, startVertex);
    validateVertex(graph, finishVertex);

    var visitedVertices = new boolean[graph.getVertexNum()];
    var edges = IntStream.range(0, graph.getVertexNum()).map(any -> -1).toArray();
    var vertexStack = new LinkedList<Integer>();
    vertexStack.addFirst(startVertex);
    visitedVertices[startVertex] = true;
    while (!vertexStack.isEmpty()) {
      var vertex = vertexStack.removeFirst();
      if (vertex == finishVertex) {
        break;
      } else {
        graph.adjacentVertices(vertex).forEach(adjacentVertex -> {
          if (!visitedVertices[adjacentVertex]) {
            visitedVertices[adjacentVertex] = true;
            edges[adjacentVertex] = vertex;
            vertexStack.addFirst(adjacentVertex);
          }
        });
      }
    }
    return visitedVertices[finishVertex] ?
        Optional.of(path(finishVertex, edges)) : Optional.empty();
  }

  private static void validateGraph(Graph graph) {
    if (graph == null) {
      throw new IllegalArgumentException("Graph cannot be null");
    }
  }

  private static void validateVertex(Graph graph, int vertex) {
    if (vertex >= graph.getVertexNum()) {
      throw new IllegalArgumentException(String.format(
          "Vertex does not belong to the graph: numberOfVertex=%s, vertex=%s",
          graph.getVertexNum(), vertex));
    }
  }

  private static DepthFirstPathSearchResult path(int finishVertex, int[] edges) {
    var vertices = new LinkedList<Integer>();
    vertices.addFirst(finishVertex);
    var vertex = edges[finishVertex];
    while (vertex != -1) {
      vertices.addFirst(vertex);
      vertex = edges[vertex];
    }

    var numberOfVisitedEdges = Arrays.stream(edges).filter(Objects::nonNull).count();
    return new DepthFirstPathSearchResult(vertices, numberOfVisitedEdges);
  }

  public record DepthFirstPathSearchResult(
      List<Integer> vertices,
      long numberOfVisitedEdges) {

  }
}