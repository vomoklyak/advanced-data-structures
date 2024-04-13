package com.lesson.ads.chapter14;

import com.lesson.ads.chapter14.BreadthFirstPathSearch.BreadthFirstPathSearchResult;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BreadthFirstPathSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var graph = new Graph(6);
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(0, 3);
    graph.addEdge(1, 2);
    graph.addEdge(2, 5);
    graph.addEdge(2, 3);
    graph.addEdge(3, 4);
    graph.addEdge(4, 5);

    // When
    final var result =
        BreadthFirstPathSearch.search(graph, 0, 5);

    // Then
    Assertions.assertThat(result).get()
        .returns(List.of(0, 2, 5), BreadthFirstPathSearchResult::vertices)
        .returns(6L, BreadthFirstPathSearchResult::numberOfVisitedEdges);
  }

  @Test
  void shouldSearchCaseEmptyResult() {
    // Given
    final var graph = new Graph(6);
    graph.addEdge(0, 1);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    graph.addEdge(4, 5);

    // When
    final var result =
        BreadthFirstPathSearch.search(graph, 0, 5);

    // Then
    Assertions.assertThat(result).isEmpty();
  }
}