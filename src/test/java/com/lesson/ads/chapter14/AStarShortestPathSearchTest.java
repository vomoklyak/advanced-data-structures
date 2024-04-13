package com.lesson.ads.chapter14;

import com.lesson.ads.chapter14.AStarShortestPathSearch.AStarShortestPathSearchResult;
import com.lesson.ads.chapter14.EdgeWeightedDirectedGraph.DirectedEdge;
import com.lesson.ads.util.Point;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AStarShortestPathSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var finishPoint = new Point(6D, 0D);
    final var vertexToPoint = Map.of(
        0, new Point(0D, 0D),
        1, new Point(-1D, 0D),
        2, new Point(-2D, 0D),
        3, new Point(0D, 1D),
        4, new Point(0D, 2D),
        5, new Point(0D, -1D),
        6, new Point(0D, -2D),
        7, new Point(1D, 0D),
        8, new Point(3D, 0D),
        9, finishPoint
    );
    final Function<DirectedEdge, Double> heuristic =
        edge -> vertexToPoint.get(edge.getToVertex()).distance(finishPoint);

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
        AStarShortestPathSearch.search(graph, 0, 9, heuristic);

    // Then
    Assertions.assertThat(result).get()
        .returns(List.of(0, 7, 8, 9), AStarShortestPathSearchResult::vertices)
        .returns(6D, AStarShortestPathSearchResult::distance)
        .returns(6L, AStarShortestPathSearchResult::numberOfVisitedEdges);
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
        AStarShortestPathSearch.search(graph, 2, 3, any -> 0D);

    // Then
    Assertions.assertThat(result).isEmpty();
  }
}