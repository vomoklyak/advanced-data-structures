package com.lesson.ads.chapter14;

import java.util.Collection;
import java.util.HashSet;
import lombok.Getter;

public class Graph {

  @Getter
  private final int vertexNum;
  private final Collection<Integer>[] adjacentVertices;
  @Getter
  private int edgeNum;

  public Graph(int vertexNum) {
    this.vertexNum = vertexNum;
    //noinspection unchecked
    this.adjacentVertices = new HashSet[vertexNum];
    for (int index = 0; index < adjacentVertices.length; index++) {
      adjacentVertices[index] = new HashSet<>();
    }
  }

  public void addEdge(int left, int right) {
    adjacentVertices[left].add(right);
    adjacentVertices[right].add(left);
    edgeNum++;
  }

  public Collection<Integer> adjacentVertices(int vertex) {
    return adjacentVertices[vertex];
  }
}