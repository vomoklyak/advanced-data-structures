package com.lesson.ads.chapter17;

import com.lesson.ads.util.Point;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class TspSimulatedAnnealingSearch extends AbstractSimulatedAnnealingSearch {

  public static final double NONE = Double.MAX_VALUE;

  private final Random random;

  public TspSimulatedAnnealingSearch() {
    this(new Random());
  }

  private TspSimulatedAnnealingSearch(Random random) {
    super(random);
    this.random = random;
  }

  public TspSimulatedAnnealingResponse search(TspSimulatedAnnealingRequest request) {
    validateNumberOfCities(request.numberOfCities());
    validateIntercityRoadLengths(request.numberOfCities(), request.intercityRoadLengths());
    validateMaxNumberOfIterations(request.maxNumberOfIterations());
    validateTemperature(request.temperature());
    validateAlphaRate(request.alphaRate());
    validateBetaRate(request.betaRate());

    var numberOfCities = request.numberOfCities();
    var intercityRoadLengths = request.intercityRoadLengths();

    // tour length
    Function<Point, Double> costFunction = point -> IntStream.range(0, point.dimension())
        .mapToDouble(index -> {
          var tourCity = (int) point.coordinate(index);
          var nextTourCity = (int) point.coordinate((index + 1) % point.dimension());
          return intercityRoadLengths[tourCity][nextTourCity];
        })
        .sum();

    // initial tour
    var tour = IntStream.range(0, numberOfCities + 1)
        .mapToDouble(cityIndex -> cityIndex % numberOfCities)
        .toArray();

    var result = super.search(
        request.maxNumberOfIterations(),
        new Point(tour),
        request.temperature(),
        stepFunction(),
        acceptanceFunction(request.alphaRate(), costFunction),
        temperatureCoolingFunction(request.betaRate()));
    return new TspSimulatedAnnealingResponse(
        request.numberOfCities(),
        request.intercityRoadLengths(),
        request.maxNumberOfIterations(),
        request.temperature(),
        request.alphaRate(),
        request.betaRate(),
        result.coordinateStream().mapToInt(value -> (int) value).toArray(),
        costFunction.apply(result)
    );
  }

  private void validateNumberOfCities(int numberOfCities) {
    if (numberOfCities < 3) {
      throw new IllegalArgumentException(String.format(
          "Number of cities cannot be less then 3: numberOfCities=%s", numberOfCities));
    }
  }

  private void validateIntercityRoadLengths(
      int numberOfCities, double[][] intercityRoadLengths) {
    if (intercityRoadLengths == null) {
      throw new IllegalArgumentException("Intercity road lengths cannot be null");
    }
    if (intercityRoadLengths.length != numberOfCities) {
      throw new IllegalArgumentException(String.format(
          "Intercity road lengths should be equal to number of cities: intercityRoadLengths=%s, numberOfCities=%s",
          intercityRoadLengths.length, numberOfCities));
    }
    Arrays.stream(intercityRoadLengths)
        .forEach(cityRoadLengths -> {
          if (cityRoadLengths == null) {
            throw new IllegalArgumentException("City intercity road lengths cannot be null");
          }
          if (cityRoadLengths.length != numberOfCities) {
            throw new IllegalArgumentException(String.format(
                "City intercity road lengths should be equal to number of cities: cityRoadLengths=%s, numberOfCities=%s",
                Arrays.toString(cityRoadLengths), numberOfCities));
          }
          Arrays.stream(cityRoadLengths)
              .filter(value -> value < 0.0D)
              .findFirst()
              .ifPresent(value -> {
                throw new IllegalArgumentException(String.format(
                    "City intercity road length should be non-negative: cityRoadLength=%s", value));
              });
        });
  }

  private static void validateMaxNumberOfIterations(int maxNumberOfIterations) {
    if (maxNumberOfIterations < 1) {
      throw new IllegalArgumentException(String.format(
          "Max number of iterations should be positive: maxNumberOfIterations=%s",
          maxNumberOfIterations));
    }
  }

  private static void validateTemperature(double temperature) {
    if (temperature <= 0) {
      throw new IllegalArgumentException(String.format(
          "Temperature should be positive: temperature=%s", temperature));
    }
  }

  private static void validateAlphaRate(double alphaRate) {
    if (alphaRate <= 0) {
      throw new IllegalArgumentException(String.format(
          "Alpha rate should be positive: alphaRate=%s", alphaRate));
    }
  }

  private static void validateBetaRate(double betaRate) {
    if (betaRate <= 0 || betaRate >= 1) {
      throw new IllegalArgumentException(String.format(
          "Beta rate should belong to the interval (0, 1): betaRate=%s", betaRate));
    }
  }

  private StepFunction stepFunction() {
    return (point, temperature) -> {
      var randomFloat = random.nextFloat(0, 1);
      var nextPointCoordinates = point.coordinateStream().toArray();
      if (randomFloat < 0.1D) {
        int randomCoordinateIndex = random.nextInt(1, point.dimension() - 3);
        swap(randomCoordinateIndex, randomCoordinateIndex + 1, nextPointCoordinates);
      } else if (randomFloat < 0.8D) {
        int randomCoordinateIndexOne = random.nextInt(1, point.dimension() - 2);
        int randomCoordinateIndexTwo = random.nextInt(1, point.dimension() - 2);
        swap(randomCoordinateIndexOne, randomCoordinateIndexTwo, nextPointCoordinates);
      } else {
        shuffle(nextPointCoordinates);
      }
      return new Point(nextPointCoordinates);
    };
  }

  private void shuffle(double[] array) {
    for (int index = array.length - 2; index > 1; index--) {
      swap(index, random.nextInt(1, index + 1), array);
    }
  }

  private void swap(int indexOne, int indexTwo, double[] array) {
    var value = array[indexOne];
    array[indexOne] = array[indexTwo];
    array[indexTwo] = value;
  }

  private AcceptanceFunction acceptanceFunction(
      double alphaRate, Function<Point, Double> costFunction) {
    return (startPoint, endPoint, temperature) -> {
      var costDelta = costFunction.apply(endPoint) - costFunction.apply(startPoint);
      if (costDelta >= 0) {
        return Math.exp(-(costDelta) / (alphaRate * temperature));
      } else {
        return 1;
      }
    };
  }

  private TemperatureCoolingFunction temperatureCoolingFunction(double betaRate) {
    return temperature -> betaRate * temperature;
  }

  public record TspSimulatedAnnealingRequest(
      int numberOfCities,
      double[][] intercityRoadLengths,
      int maxNumberOfIterations,
      double temperature,
      double alphaRate,
      double betaRate) {

  }

  public record TspSimulatedAnnealingResponse(
      int numberOfCities,
      double[][] intercityRoadLengths,
      int maxNumberOfIterations,
      double temperature,
      double alphaRate,
      double betaRate,
      int[] tour,
      double tourRoadLength) {

    @Override
    public String toString() {
      return String.format("TspSimulatedAnnealingResponse[tour=%s, tourRoadLength=%s]",
          Arrays.toString(tour), tourRoadLength);
    }
  }
}
