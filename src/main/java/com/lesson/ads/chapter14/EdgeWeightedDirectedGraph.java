package com.lesson.ads.chapter14;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class EdgeWeightedDirectedGraph {

  @Getter
  private final int vertexNum;
  private final Collection<DirectedEdge>[] adjacentEdges;
  @Getter
  private int edgeNum;

  public EdgeWeightedDirectedGraph(int vertexNum) {
    this.vertexNum = vertexNum;
    //noinspection unchecked
    this.adjacentEdges = new HashSet[vertexNum];
    for (int index = 0; index < adjacentEdges.length; index++) {
      adjacentEdges[index] = new HashSet<>();
    }
  }

  public void addEdge(DirectedEdge edge) {
    adjacentEdges[edge.getFromVertex()].add(edge);
    edgeNum++;
  }

  public Collection<DirectedEdge> adjacentEdges(int vertex) {
    return adjacentEdges[vertex];
  }

  public Collection<DirectedEdge> edges() {
    return Arrays.stream(adjacentEdges)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Data
  @RequiredArgsConstructor
  public static class DirectedEdge implements Comparable<DirectedEdge> {

    private final int fromVertex;
    private final int toVertex;
    private final double distance;

    @Override
    public int compareTo(DirectedEdge that) {
      return Double.compare(this.distance, that.distance);
    }
  }
}