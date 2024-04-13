package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.OpticsClusterSearch.OpticsClusterSearchResult;
import com.lesson.ads.util.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OpticsClusterSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 3D;
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
    final var result = new OpticsClusterSearch(1)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, OpticsClusterSearchResult::points)
        .returns(new int[]{-1, 0, 0, 0, 0, 1, 1},
            searchResult -> searchResult.pointClusterIndexes(2))
        .returns(new int[]{0, 0, 0, 0, 0, 0, 0},
            searchResult -> searchResult.pointClusterIndexes(3));
  }

  @Test
  void shouldSearchCaseEdgePoint() {
    // Given
    final var coreNumberOfNeighbors = 3;
    final var maxCoreRadius = 3D;
    final var points = new Point[]{
        new Point(1D),
        new Point(4D),
        new Point(5D),
        new Point(6D),
        new Point(11D),
        new Point(12D)
    };

    // When
    final var result = new OpticsClusterSearch(1)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, OpticsClusterSearchResult::points)
        .returns(new int[]{0, 0, 0, 0, -1, -1},
            searchResult -> searchResult.pointClusterIndexes(maxCoreRadius));
  }

  @Test
  void shouldSearchCaseSpiral() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 5D;
    final var points = Datasets.spiral();

    // When
    final var result = new OpticsClusterSearch(2)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(1080,
            searchResult -> searchResult.points().length)
        .returns(1080,
            searchResult -> searchResult.pointClusterIndexes(maxCoreRadius).length);
  }

  @Test
  void shouldSearchCaseConcentricCircles() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 17D;
    final var points = Datasets.concentricCircles();

    // When
    final var result = new OpticsClusterSearch(2)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(2404,
            searchResult -> searchResult.points().length)
        .returns(2404,
            searchResult -> searchResult.pointClusterIndexes(maxCoreRadius).length);
  }

  @Test
  void shouldSearchCaseParallelLines() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 2D;
    final var points = Datasets.parallelLines();

    // When
    final var result = new OpticsClusterSearch(2)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(902,
            searchResult -> searchResult.points().length)
        .returns(902,
            searchResult -> searchResult.pointClusterIndexes(maxCoreRadius).length);
  }

  @Test
  void shouldSearchCaseLines() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 2D;
    final var points = Datasets.lines();

    // When
    final var result = new OpticsClusterSearch(2)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(902, searchResult ->
            searchResult.points().length)
        .returns(902, searchResult ->
            searchResult.pointClusterIndexes(maxCoreRadius).length);
  }

  @Test
  void shouldSearchCaseHeterogeneousDensitySquares() {
    // Given
    final var coreNumberOfNeighbors = 2;
    final var maxCoreRadius = 2D;
    final var points = Datasets.heterogeneousDensitySquares();

    // When
    final var result = new OpticsClusterSearch(2)
        .search(coreNumberOfNeighbors, maxCoreRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(484, searchResult ->
            searchResult.points().length)
        .returns(484, searchResult ->
            searchResult.pointClusterIndexes(maxCoreRadius).length);
  }
}