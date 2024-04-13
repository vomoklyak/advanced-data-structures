package com.lesson.ads.chapter18;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class TspGeneticAlgorithmSearch extends AbstractGeneticAlgorithmSearch {

  private final Random random;

  public TspGeneticAlgorithmSearch() {
    this.random = new Random();
  }

  public TspGeneticAlgorithmSearchResponse search(TspGeneticAlgorithmSearchRequest request) {
    validateNumberOfCities(request.numberOfCities);
    validateIntercityRoadLengths(request.numberOfCities, request.intercityRoadLengths);
    validateMaxNumberOfIterations(request.maxNumberOfIterations);
    validateAlphaRate(request.alphaRate);
    validateOmegaRate(request.omegaRate);
    validatePopulationSize(request.populationSize);

    var config = naturalSelectionConfig(request);
    var resultChromosome = super.search(config).orElseThrow();
    return new TspGeneticAlgorithmSearchResponse(
        request.numberOfCities,
        request.intercityRoadLengths,
        request.maxNumberOfIterations,
        request.populationSize,
        request.alphaRate,
        request.omegaRate,
        request.crossoverProbability,
        request.mutationProbability,
        Arrays.stream(resultChromosome.gens()).mapToInt(value -> (int) value).toArray(),
        fitnessFunction(request).apply(resultChromosome)
    );
  }

  private void validateNumberOfCities(int numberOfCities) {
    if (numberOfCities < 3) {
      throw new IllegalArgumentException(String.format(
          "Number of cities cannot be less then 3: numberOfCities=%s", numberOfCities));
    }
  }

  private void validateIntercityRoadLengths(int numberOfCities, double[][] intercityRoadLengths) {
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

  private static void validateAlphaRate(double alphaRate) {
    if (alphaRate < 0 || alphaRate > 1) {
      throw new IllegalArgumentException(String.format(
          "Alpha rate should belong to the interval [0, 1]: alphaRate=%s", alphaRate));
    }
  }

  private static void validateOmegaRate(double omegaRate) {
    if (omegaRate < 0.0D || omegaRate > 1.0D) {
      throw new IllegalArgumentException(String.format(
          "Omega rate should belong to the interval [0, 1]: omegaRate=%s", omegaRate));
    }
  }

  private static void validatePopulationSize(int populationSize) {
    if (populationSize <= 0) {
      throw new IllegalArgumentException(String.format(
          "Population size should be positive: populationSize=%s", populationSize));
    }
  }

  private NaturalSelectionConfig naturalSelectionConfig(TspGeneticAlgorithmSearchRequest request) {
    return new NaturalSelectionConfig(
        request.maxNumberOfIterations,
        request.alphaRate,
        request.omegaRate,
        request.populationSize,
        chromosomeSupplier(request),
        inverseFitnessFunction(request),
        crossoverPairSelectFunction(),
        crossoverFunction(request),
        List.of(mutationFunction())
    );
  }

  private ChromosomeSupplier chromosomeSupplier(TspGeneticAlgorithmSearchRequest request) {
    // initial tour
    var initialGens = IntStream.range(0, request.numberOfCities + 1)
        .mapToDouble(cityIndex -> cityIndex % request.numberOfCities)
        .toArray();
    return () -> {
      var gens = initialGens.clone();
      shuffle(gens);
      return new Chromosome(gens);
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

  /**
   * Inversed distance is used, because fitness function in genetic algorithm is oriented on max,
   * but TSP cost function is oriented on min (max distance is min for 1/distance, distance >= 0).
   */
  private FitnessFunction inverseFitnessFunction(TspGeneticAlgorithmSearchRequest request) {
    return chromosome -> IntStream.range(0, chromosome.numberOfGens())
        .mapToDouble(index -> {
          // city
          var gen = (int) chromosome.gen(index);
          // next city
          var nextGen = (int) chromosome.gen((index + 1) % chromosome.numberOfGens());
          var roadLength = request.intercityRoadLengths[gen][nextGen];
          return roadLength == 0.0D ? 0 : 1 / request.intercityRoadLengths[gen][nextGen];
        })
        .sum();
  }

  private FitnessFunction fitnessFunction(TspGeneticAlgorithmSearchRequest request) {
    return chromosome -> IntStream.range(0, chromosome.numberOfGens())
        .mapToDouble(index -> {
          // city
          var gen = (int) chromosome.gen(index);
          // next city
          var nextGen = (int) chromosome.gen((index + 1) % chromosome.numberOfGens());
          return request.intercityRoadLengths[gen][nextGen];
        })
        .sum();
  }

  private CrossoverPairSelectFunction crossoverPairSelectFunction() {
    return population -> {
      var randomChromosomeIndex = random.nextInt(0, population.size());
      return population.get(randomChromosomeIndex);
    };
  }

  private CrossoverFunction crossoverFunction(TspGeneticAlgorithmSearchRequest request) {
    return (leftChromosome, rightChromosome) -> {
      if (random.nextDouble() < request.crossoverProbability()) {
        var crossedGenLinkedSet = new LinkedHashSet<Double>();
        var splitIndex = random.nextInt(1, request.numberOfCities);
        // copy l-chromosome gens till split index (in l-chromosome order)
        Arrays.stream(leftChromosome.gens()).limit(splitIndex).forEach(crossedGenLinkedSet::add);
        // copy r-chromosome gens not present yet (in r-chromosome order)
        Arrays.stream(rightChromosome.gens()).forEach(crossedGenLinkedSet::add);
        // tour has duplicates (start and end city)
        var crossedGens = Stream.concat(crossedGenLinkedSet.stream(), Stream.of(0.0D))
            .mapToDouble(value -> value)
            .toArray();
        return new Chromosome(crossedGens);
      } else {
        return random.nextBoolean() ? leftChromosome : rightChromosome;
      }
    };
  }

  private MutationFunction mutationFunction() {
    return chromosome -> {
      var mutatedGens = chromosome.gens().clone();
      int randomGenIndexOne = random.nextInt(1, mutatedGens.length - 2);
      int randomGenIndexTwo = random.nextInt(1, mutatedGens.length - 2);
      swap(randomGenIndexOne, randomGenIndexTwo, mutatedGens);
      return new Chromosome(mutatedGens);
    };
  }

  public record TspGeneticAlgorithmSearchRequest(
      int numberOfCities,
      double[][] intercityRoadLengths,
      int maxNumberOfIterations,
      int populationSize,
      double alphaRate,
      double omegaRate,
      double crossoverProbability,
      double mutationProbability
  ) {

  }

  public record TspGeneticAlgorithmSearchResponse(
      int numberOfCities,
      double[][] intercityRoadLengths,
      int maxNumberOfIterations,
      int populationSize,
      double alphaRate,
      double omegaRate,
      double crossoverProbability,
      double mutationProbability,
      int[] tour,
      double tourRoadLength
  ) {

    @Override
    public String toString() {
      return String.format("TspGeneticAlgorithmSearchResponse[tourRoadLength=%s, tour=%s]",
          tourRoadLength, Arrays.toString(tour));
    }
  }
}