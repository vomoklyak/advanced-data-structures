package com.lesson.ads.chapter16;

import com.lesson.ads.chapter16.LinearRegressionGradientDescentSearch.LinearRegressionSearchRequest;
import com.lesson.ads.chapter16.LinearRegressionGradientDescentSearch.LinearRegressionSearchResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LinearRegressionGradientDescentSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var dataset = new double[][]{
        {1714D, 2.4D},
        {1664, 2.52D},
        {1760, 2.54D},
        {1685, 2.74D},
        {1693, 2.83D},
        {1670, 2.91D},
        {1764, 3.0D},
        {1764, 3.0D},
        {1792, 3.01D},
        {1850, 3.01D},
    };
    final var request = new LinearRegressionSearchRequest(
        2,
        dataset,
        1000000,
        0.00000061D,
        0.9999D
    );

    // When
    final var result =
        LinearRegressionGradientDescentSearch.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(request.dimension(), LinearRegressionSearchResponse::dimension)
        .returns(request.dataset(), LinearRegressionSearchResponse::dataset);
    Assertions.assertThat(result.parameters(5))
        .containsExactly(0.00196, -0.60191);
  }

  @Test
  void shouldSearchCase2D() {
    // Given
    final var dataset = new double[][]{
        {1, 3},
        {2, 5},
        {3, 7},
        {4, 9},
        {5, 11}
    };
    final var request = new LinearRegressionSearchRequest(
        2,
        dataset,
        1000000,
        0.01D,
        0.1D
    );

    // When
    final var result =
        LinearRegressionGradientDescentSearch.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(request.dimension(), LinearRegressionSearchResponse::dimension)
        .returns(request.dataset(), LinearRegressionSearchResponse::dataset);
    Assertions.assertThat(result.parameters(5))
        .containsExactly(2.0D, 1.0D);
  }

  @Test
  void shouldSearchCase3D() {
    // Given
    final var dataset = new double[][]{
        {1, 3, 5},
        {2, 5, 8},
        {3, 7, 11},
        {4, 9, 14},
        {5, 11, 17}
    };
    final var request = new LinearRegressionSearchRequest(
        3,
        dataset,
        1000000,
        0.01D,
        0.9D
    );

    // When
    final var result =
        LinearRegressionGradientDescentSearch.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(request.dimension(), LinearRegressionSearchResponse::dimension)
        .returns(request.dataset(), LinearRegressionSearchResponse::dataset);
    Assertions.assertThat(result.parameters(5))
        .containsExactly(0.33333D, 1.33333D, 0.66667D);
  }
}