package com.lesson.ads.chapter14;

import com.lesson.ads.chapter14.DijkstraShortestPathSearch.DijkstraShortestPathSearchResult;
import com.lesson.ads.chapter14.EdgeWeightedDirectedGraph.DirectedEdge;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DijkstraShortestPathSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var graph = new EdgeWeightedDirectedGraph(10);
    graph.addEdge(new DirectedEdge(0, 1, 1D));
    graph.addEdge(new DirectedEdge(1, 2, 1D));
    graph.addEdge(new DirectedEdge(0, 3, 1D));
    graph.addEdge(new DirectedEdge(3, 4, 1D));
    graph.addEdge(new DirectedEdge(0, 5, 1D));
    graph.addEdge(new DirectedEdge(5, 6, 1D));
    graph.addEdge(new DirectedEdge(0, 7, 1D));
    graph.addEdge(new DirectedEdge(7, 8, 2D));
    graph.addEdge(new DirectedEdge(8, 9, 3D));

    // When
    final var result =
        DijkstraShortestPathSearch.search(graph, 0, 9);

    // Then
    Assertions.assertThat(result).get()
        .returns(List.of(0, 7, 8, 9), DijkstraShortestPathSearchResult::vertices)
        .returns(6D, DijkstraShortestPathSearchResult::distance)
        .returns(9L, DijkstraShortestPathSearchResult::numberOfVisitedEdges);
  }

  @Test
  void shouldSearchCaseEmptyResult() {
    // Given
    final var graph = new EdgeWeightedDirectedGraph(10);
    graph.addEdge(new DirectedEdge(0, 1, 1D));
    graph.addEdge(new DirectedEdge(1, 2, 1D));
    graph.addEdge(new DirectedEdge(0, 3, 1D));
    graph.addEdge(new DirectedEdge(3, 4, 1D));
    graph.addEdge(new DirectedEdge(0, 5, 1D));
    graph.addEdge(new DirectedEdge(5, 6, 1D));
    graph.addEdge(new DirectedEdge(0, 7, 1D));
    graph.addEdge(new DirectedEdge(7, 8, 2D));
    graph.addEdge(new DirectedEdge(8, 9, 3D));

    // When
    final var result =
        DijkstraShortestPathSearch.search(graph, 2, 3);

    // Then
    Assertions.assertThat(result).isEmpty();
  }
}