package com.lesson.ads.chapter17;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TspBruteForceSearch {

  public static final double NONE = Double.POSITIVE_INFINITY;

  public static TspBruteForceSearchResponse search(TspBruteForceSearchRequest request) {
    validateNumberOfCities(request.numberOfCities());
    validateIntercityRoadLengths(request.numberOfCities(), request.intercityRoadLengths());

    var numberOfCities = request.numberOfCities();
    var intercityRoadLengths = request.intercityRoadLengths();

    // tour length
    Function<Integer[], Double> tourLengthFun = tour -> IntStream.range(0, tour.length - 1)
        .mapToDouble(index -> intercityRoadLengths[tour[index]][tour[(index + 1) % numberOfCities]])
        .sum();

    // initial tour
    Integer[] tour = IntStream.range(0, numberOfCities + 1)
        .map(cityIndex -> cityIndex % numberOfCities)
        .boxed()
        .toArray(Integer[]::new);

    boolean[] availableCities = new boolean[numberOfCities];
    Arrays.fill(availableCities, true);
    availableCities[0] = false;

    tour = search(1, tour, availableCities, tourLengthFun, tour);
    return new TspBruteForceSearchResponse(
        request.numberOfCities(),
        request.intercityRoadLengths(),
        Arrays.stream(tour).mapToInt(Integer::intValue).toArray(),
        tourLengthFun.apply(tour)
    );
  }

  private static void validateNumberOfCities(int numberOfCities) {
    if (numberOfCities < 3) {
      throw new IllegalArgumentException(String.format(
          "Number of cities cannot be less then 3: numberOfCities=%s", numberOfCities));
    }
  }

  private static void validateIntercityRoadLengths(
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

  private static Integer[] search(
      int tourCityIndex,
      Integer[] tour,
      boolean[] availableCities,
      Function<Integer[], Double> tourRoadLengthFun,
      Integer[] minRoadLengthTour) {
    if (tourCityIndex == tour.length - 1) {
      return tourRoadLengthFun.apply(tour) < tourRoadLengthFun.apply(minRoadLengthTour) ?
          tour : minRoadLengthTour;
    } else {
      for (int city = 1; city < availableCities.length; city++) {
        if (availableCities[city]) {
          var clonedTour = tour.clone();
          var clonedAvailableCities = availableCities.clone();
          clonedTour[tourCityIndex] = city;
          clonedAvailableCities[city] = false;
          minRoadLengthTour = search(
              tourCityIndex + 1, clonedTour, clonedAvailableCities, tourRoadLengthFun,
              minRoadLengthTour);
        }
      }
    }
    return minRoadLengthTour;
  }

  public record TspBruteForceSearchRequest(
      int numberOfCities,
      double[][] intercityRoadLengths) {

  }

  public record TspBruteForceSearchResponse(
      int numberOfCities,
      double[][] intercityRoadLengths,
      int[] tour,
      double tourRoadLength) {

    @Override
    public String toString() {
      return String.format("TspBruteForceSearchResponse[tour=%s, tourRoadLength=%s]",
          Arrays.toString(tour), tourRoadLength);
    }
  }
}