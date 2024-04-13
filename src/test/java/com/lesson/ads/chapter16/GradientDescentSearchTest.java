package com.lesson.ads.chapter16;

import com.lesson.ads.util.Point;
import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GradientDescentSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final int maxNumberOfIterations = 100;
    final Point startPoint = new Point(0.0D, 0.0D);
    final double learningRate = 0.9D;
    // z = (x-10)^2 + (y-100)^2
    final List<Function<Point, Double>> partialDerivatives = List.of(
        point -> 2.0D * (point.coordinate(0) - 10.0D),
        point -> 2.0D * (point.coordinate(1) - 100.0D)
    );
    final var sut = new GradientDescentSearch(2);

    // When
    final var result = sut.search(
        maxNumberOfIterations, startPoint, learningRate, partialDerivatives);

    // Then
    Assertions.assertThat(result).isEqualTo(new Point(9.999999997962963D, 99.99999997962964D));
  }

  @Test
  void shouldSearchCaseMomentum() {
    // Given
    final int maxNumberOfIterations = 100;
    final Point startPoint = new Point(0.0D, 0.0D);
    final double learningRate = 0.9D;
    final double momentumRate = 0.2D;
    // z = (x-10)^2 + (y-100)^2
    final List<Function<Point, Double>> partialDerivatives = List.of(
        point -> 2.0D * (point.coordinate(0) - 10.0D),
        point -> 2.0D * (point.coordinate(1) - 100.0D)
    );
    final var sut = new GradientDescentSearch(2);

    // When
    final var result = sut.search(
        maxNumberOfIterations, startPoint, learningRate, momentumRate, partialDerivatives);

    // Then
    Assertions.assertThat(result).isEqualTo(new Point(10.0D, 100.0D));
  }
}