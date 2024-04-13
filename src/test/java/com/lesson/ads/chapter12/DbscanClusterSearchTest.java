package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.DbscanClusterSearch.DbscanClusterSearchResult;
import com.lesson.ads.util.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DbscanClusterSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 2D;
    final var points = new Point[]{
        new Point(1D),
        new Point(4D),
        new Point(5D),
        new Point(6D),
        new Point(8D),
        new Point(11D),
        new Point(12D)
    };

    // When
    final var result = new DbscanClusterSearch(1)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, DbscanClusterSearchResult::points)
        .returns(new int[]{-1, 0, 0, 0, 0, 1, 1}, DbscanClusterSearchResult::pointClusterIndexes)
        .returns(2, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseEdgePoint() {
    // Given
    final var coreNumberOfNeighbors = 3;
    final var coreRadius = 3D;
    final var points = new Point[]{
        new Point(1D),
        new Point(4D),
        new Point(5D),
        new Point(6D),
        new Point(11D),
        new Point(12D)
    };

    // When
    final var result = new DbscanClusterSearch(1)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, DbscanClusterSearchResult::points)
        .returns(new int[]{0, 0, 0, 0, -1, -1}, DbscanClusterSearchResult::pointClusterIndexes)
        .returns(1, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseSpiral() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 5D;
    final var points = Datasets.spiral();

    // When
    final var result = new DbscanClusterSearch(2)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(1080, searchResult -> searchResult.points().length)
        .returns(1080, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(1, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseConcentricCircles() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 17D;
    final var points = Datasets.concentricCircles();

    // When
    final var result = new DbscanClusterSearch(2)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(2404, searchResult -> searchResult.points().length)
        .returns(2404, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseParallelLines() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 2D;
    final var points = Datasets.parallelLines();

    // When
    final var result = new DbscanClusterSearch(2)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(902, searchResult -> searchResult.points().length)
        .returns(902, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseLines() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 2D;
    final var points = Datasets.lines();

    // When
    final var result = new DbscanClusterSearch(2)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(902, searchResult -> searchResult.points().length)
        .returns(902, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, DbscanClusterSearchResult::numberOfClusters);
  }

  @Test
  void shouldSearchCaseHeterogeneousDensitySquares() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var coreRadius = 2D;
    final var points = Datasets.heterogeneousDensitySquares();

    // When
    final var result = new DbscanClusterSearch(2)
        .search(coreNumberOfNeighbors, coreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(484, searchResult -> searchResult.points().length)
        .returns(484, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, DbscanClusterSearchResult::numberOfClusters);
  }
}