package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.KMeansClusterSearch.KMeansClusterSearchResult;
import com.lesson.ads.util.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class KMeansClusterSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var maxNumberOfClusters = 1;
    final var points = new Point[]{
        new Point(1D),
        new Point(2D),
        new Point(3D),
        new Point(4D)
    };

    // When
    final var result = new KMeansClusterSearch(1)
        .search(maxNumberOfIterations, maxNumberOfClusters, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, KMeansClusterSearchResult::points)
        .returns(new int[]{0, 0, 0, 0}, KMeansClusterSearchResult::pointClusterIndexes)
        .returns(2, KMeansClusterSearchResult::numberOfIterations);
    Assertions.assertThat(result.clusterCentroids()).contains(new Point(2.5D));
  }

  @Test
  void shouldSearchCaseSpiral() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var points = Datasets.spiral();
    final var initialCentroids = new Point[]{
        new Point(-7D, 81D),
        new Point(122D, 94D),
        new Point(-106D, -83D)
    };

    // When
    final var result = new KMeansClusterSearch(2)
        .search(maxNumberOfIterations, initialCentroids, points);

    // Then
    Assertions.assertThat(result)
        .returns(1080, searchResult -> searchResult.points().length)
        .returns(1080, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(3, searchResult -> searchResult.clusterCentroids().length)
        .returns(31, KMeansClusterSearchResult::numberOfIterations);
  }

  @Test
  void shouldSearchCaseConcentricCircles() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var points = Datasets.concentricCircles();
    final var initialCentroids = new Point[]{
        new Point(0.5D, 130D),
        new Point(0.5D, -130D)
    };

    // When
    final var result = new KMeansClusterSearch(2)
        .search(maxNumberOfIterations, initialCentroids, points);

    // Then
    Assertions.assertThat(result)
        .returns(2404, searchResult -> searchResult.points().length)
        .returns(2404, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, searchResult -> searchResult.clusterCentroids().length)
        .returns(2, KMeansClusterSearchResult::numberOfIterations);
  }

  @Test
  void shouldSearchCaseParallelLines() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var points = Datasets.parallelLines();
    final var initialCentroids = new Point[]{
        new Point(0D, 20D),
        new Point(0D, -20D)
    };

    // When
    final var result = new KMeansClusterSearch(2)
        .search(maxNumberOfIterations, initialCentroids, points);

    // Then
    Assertions.assertThat(result)
        .returns(902, searchResult -> searchResult.points().length)
        .returns(902, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(initialCentroids, KMeansClusterSearchResult::clusterCentroids)
        .returns(2, KMeansClusterSearchResult::numberOfIterations);
  }

  @Test
  void shouldSearchCaseLines() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var points = Datasets.lines();
    final var initialCentroids = new Point[]{
        new Point(0D, 20D),
        new Point(0D, -20D)
    };

    // When
    final var result = new KMeansClusterSearch(2)
        .search(maxNumberOfIterations, initialCentroids, points);

    // Then
    Assertions.assertThat(result)
        .returns(902, searchResult -> searchResult.points().length)
        .returns(902, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(2, searchResult -> searchResult.clusterCentroids().length)
        .returns(6, KMeansClusterSearchResult::numberOfIterations);
  }

  @Test
  void shouldSearchCaseHeterogeneousDensitySquares() {
    // Given
    final var maxNumberOfIterations = 1000;
    final var points = Datasets.heterogeneousDensitySquares();
    final var initialCentroids = new Point[]{
        new Point(10D, 10D),
        new Point(21D, 21D),
        new Point(50D, 50D),
        new Point(200D, 20D)
    };

    // When
    final var result = new KMeansClusterSearch(2)
        .search(maxNumberOfIterations, initialCentroids, points);

    // Then
    Assertions.assertThat(result)
        .returns(484, searchResult -> searchResult.points().length)
        .returns(484, searchResult -> searchResult.pointClusterIndexes().length)
        .returns(4, searchResult -> searchResult.clusterCentroids().length)
        .returns(4, KMeansClusterSearchResult::numberOfIterations);
  }
}