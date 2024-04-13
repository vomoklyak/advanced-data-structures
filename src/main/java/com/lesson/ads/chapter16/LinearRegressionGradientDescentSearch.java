package com.lesson.ads.chapter16;

import com.lesson.ads.util.Point;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class LinearRegressionGradientDescentSearch {

  // dataset X1|X2| ... |Y
  public static LinearRegressionSearchResponse search(LinearRegressionSearchRequest request) {
    validateDimension(request.dimension());
    validateDataset(request.dimension(), request.dataset());
    validateMaxNumberOfIterations(request.maxNumberOfIterations());
    validateLearningRate(request.learningRate());
    validateMomentum(request.momentumRate());

    // n
    var dimension = request.dimension;
    // n-1
    var numberOfFactors = request.dimension() - 1;
    // m
    var numberOfDatasetRows = request.dataset().length;
    var dataset = request.dataset();

    // Y - A1X1 - A1X2 ... - An
    BiFunction<Point, double[], Double> errorFun = (modelParams, dataRow) -> {
      // Y
      var error = dataRow[dimension - 1];
      // - A1X1 - A1X2 ...
      for (int index = 0; index < numberOfFactors; index++) {
        error -= modelParams.coordinate(index) * dataRow[index];
      }
      // - An
      error -= modelParams.coordinate(dimension - 1);
      return error;
    };

    // partial derivatives with respect to model parameters
    var partialDerivatives = IntStream.range(0, dimension)
        .mapToObj(index -> {
          if (index == dimension - 1) {
            // derivative with respect to An
            return (Function<Point, Double>) modelParams ->
                // -2/m * sum(Y - A1X1 -A1X2 ... - An)
                (-2.0D / numberOfDatasetRows) * Arrays.stream(dataset)
                    .mapToDouble(dataRow -> errorFun.apply(modelParams, dataRow))
                    .sum();
          } else {
            // derivative with respect to one parameter A1 ... An-1
            return (Function<Point, Double>) modelParams ->
                // -2/m * sum(Xi * (Y - A1X1 -A1X2 ... - An))
                (-2.0D / numberOfDatasetRows) * Arrays.stream(dataset)
                    .mapToDouble(dataRow -> dataRow[index] * errorFun.apply(modelParams, dataRow))
                    .sum();
          }
        })
        .collect(Collectors.toList());

    var result = new GradientDescentSearch(dimension).search(
        request.maxNumberOfIterations,
        new Point(new double[dimension]),
        request.learningRate,
        request.momentumRate,
        partialDerivatives
    );
    var parameters =
        IntStream.range(0, dimension).mapToDouble(result::coordinate).toArray();
    return new LinearRegressionSearchResponse(dimension, dataset, parameters);
  }

  private static void validateMaxNumberOfIterations(int maxNumberOfIterations) {
    if (maxNumberOfIterations < 1) {
      throw new IllegalArgumentException(String.format(
          "Max number of iterations should be positive: maxNumberOfIterations=%s",
          maxNumberOfIterations));
    }
  }

  private static void validateDataset(int dimension, double[][] dataset) {
    if (dataset == null) {
      throw new IllegalArgumentException("Dataset cannot be null");
    }
    Arrays.stream(dataset)
        .filter(Objects::nonNull)
        .filter(row -> row.length != dimension)
        .findFirst()
        .ifPresent(row -> {
          throw new IllegalArgumentException(String.format(
              "Dataset row length should be equal to dimension: row=%s, dimension=%s",
              Arrays.toString(row), dimension));
        });
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  private static void validateLearningRate(double learningRate) {
    if (learningRate <= 0 || learningRate >= 1) {
      throw new IllegalArgumentException(String.format(
          "Learning rate should belong to the interval (0, 1): learningRate=%s", learningRate));
    }
  }

  private static void validateMomentum(double momentumRate) {
    if (momentumRate < 0 || momentumRate >= 1) {
      throw new IllegalArgumentException(String.format(
          "Momentum rate should belong to the interval [0, 1): momentumRate=%s", momentumRate));
    }
  }

  public record LinearRegressionSearchRequest(
      int dimension,
      double[][] dataset,
      int maxNumberOfIterations,
      double learningRate,
      double momentumRate) {

  }

  public record LinearRegressionSearchResponse(
      int dimension,
      double[][] dataset,
      double[] parameters) {

    public double[] parameters(int scale) {
      return DoubleStream.of(parameters)
          .map(parameter ->
              BigDecimal.valueOf(parameter).setScale(scale, RoundingMode.HALF_EVEN).doubleValue())
          .toArray();
    }
  }
}