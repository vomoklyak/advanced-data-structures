package com.lesson.ads.chapter16;

import com.lesson.ads.util.Point;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class GradientDescentSearch {

  private final int dimension;

  public GradientDescentSearch(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public Point search(
      int maxNumberOfIterations,
      Point startPoint,
      double learningRate,
      List<Function<Point, Double>> partialDerivatives) {
    return search(maxNumberOfIterations,
        startPoint, learningRate, 0.0D, partialDerivatives);
  }

  public Point search(
      int maxNumberOfIterations,
      Point startPoint,
      double learningRate,
      double momentumRate,
      List<Function<Point, Double>> partialDerivatives) {
    validateMaxNumberOfIterations(maxNumberOfIterations);
    validateStartPoint(startPoint);
    validateLearningRate(learningRate);
    validateMomentum(momentumRate);
    validatePartialDerivatives(partialDerivatives);

    var point = startPoint;
    var delta = new double[dimension];
    for (int iterationIndex = 0; iterationIndex < maxNumberOfIterations; iterationIndex++) {
      var newPointCoordinates = new double[dimension];
      for (int coordinate = 0; coordinate < dimension; coordinate++) {
        delta[coordinate] = learningRate * partialDerivatives.get(coordinate).apply(point)
            + momentumRate * delta[coordinate];
        newPointCoordinates[coordinate] = point.coordinate(coordinate) - delta[coordinate];
      }
      var newPoint = new Point(newPointCoordinates);
      if (point.equals(newPoint)) {
        break;
      }
      point = newPoint;
    }
    return point;
  }

  private static void validateMaxNumberOfIterations(int maxNumberOfIterations) {
    if (maxNumberOfIterations < 1) {
      throw new IllegalArgumentException(String.format(
          "Max number of iterations should be positive: maxNumberOfIterations=%s",
          maxNumberOfIterations));
    }
  }

  private void validateStartPoint(Point point) {
    if (point == null) {
      throw new IllegalArgumentException("Points cannot be null");
    }
    if (point.dimension() != dimension) {
      throw new IllegalArgumentException(String.format(
          "Point has incorrect dimension: expectedDimension=%s, point=%s", dimension, point));
    }
  }

  private void validateLearningRate(double learningRate) {
    if (learningRate <= 0 || learningRate >= 1) {
      throw new IllegalArgumentException(String.format(
          "Learning rate should belong to the interval (0, 1): learningRate=%s", learningRate));
    }
  }

  private void validateMomentum(double momentumRate) {
    if (momentumRate < 0 || momentumRate >= 1) {
      throw new IllegalArgumentException(String.format(
          "Momentum rate should belong to the interval [0, 1): momentumRate=%s", momentumRate));
    }
  }

  private void validatePartialDerivatives(List<Function<Point, Double>> partialDerivatives) {
    if (partialDerivatives == null) {
      throw new IllegalArgumentException("Partial derivatives cannot be null");
    }
    if (partialDerivatives.size() != dimension) {
      throw new IllegalArgumentException(String.format(
          "Number of partial derivatives should be equal to dimension: numberOfPartialDerivatives=%s, dimension=%s",
          partialDerivatives.size(), dimension)
      );
    }
    Stream.ofNullable(partialDerivatives)
        .flatMap(Collection::stream)
        .filter(Objects::isNull)
        .findFirst()
        .ifPresent(any -> {
          throw new IllegalArgumentException("Partial derivative cannot be null");
        });
  }
}