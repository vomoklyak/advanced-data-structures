package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.CanopyClusterSearch.CanopyCluster;
import com.lesson.ads.chapter12.CanopyClusterSearch.CanopyClusterSearchResult;
import com.lesson.ads.util.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CanopyClusterSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var minRadius = 1D;
    final var maxRadius = 2D;
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
    final var result = new CanopyClusterSearch(1)
        .search(minRadius, maxRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, CanopyClusterSearchResult::points)
        .returns(5, searchResult -> searchResult.centroids().length);
  }

  @Test
  void shouldSearchCaseCentroids() {
    // Given
    final var minRadius = 3D;
    final var maxRadius = 4D;
    final var points = new Point[]{
        new Point(1D),
        new Point(2D),
        new Point(3D),
        new Point(8D),
        new Point(9D),
        new Point(10D)
    };

    // When
    final var result = new CanopyClusterSearch(1)
        .search(minRadius, maxRadius, points);

    // Then
    Assertions.assertThat(result)
        .returns(points, CanopyClusterSearchResult::points)
        .returns(2, searchResult -> searchResult.centroids().length);

    Assertions.assertThat(result.clusters())
        .extracting(CanopyCluster::clusterPointIndexes)
        .containsOnly(
            new int[]{0, 1, 2},
            new int[]{3, 4, 5}
        );

    Assertions.assertThat(result.centroids())
        .containsOnly(
            new Point(2D),
            new Point(9D)
        );
  }
}