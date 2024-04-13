package com.lesson.ads.chapter18;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;


public class Knapsack01GeneticAlgorithmSearch extends AbstractGeneticAlgorithmSearch {

  private static final double ITEM_SKIPPED = 0.0D;
  private static final double ITEM_SELECTED = 1.0D;

  private final Random random;

  public Knapsack01GeneticAlgorithmSearch() {
    this.random = new Random();
  }

  public Knapsack01GeneticAlgorithmSearchResponse search(
      Knapsack01GeneticAlgorithmSearchRequest request) {
    validateItems(request.items);
    validateMaxWeight(request.maxWeight);
    validateMaxNumberOfIterations(request.maxNumberOfIterations);
    validateAlphaRate(request.alphaRate);
    validateOmegaRate(request.omegaRate);
    validatePopulationSize(request.populationSize);

    var config = naturalSelectionConfig(request);
    var resultChromosome = super.search(config).orElseThrow();
    return new Knapsack01GeneticAlgorithmSearchResponse(
        request.items,
        request.maxWeight,
        request.maxNumberOfIterations,
        request.populationSize,
        request.alphaRate,
        request.omegaRate,
        request.crossoverProbability,
        request.mutationProbability,
        selectedItems(resultChromosome, request)
    );
  }

  private static void validateItems(List<Item> items) {
    if (items == null) {
      throw new IllegalArgumentException("Items cannot be null");
    }
    if (items.size() == 0) {
      throw new IllegalArgumentException("Items cannot be empty");
    }
    items.forEach(item -> {
      if (item.name == null) {
        throw new IllegalArgumentException(String.format(
            "Item name cannot be null: item=%s", item));
      }
      if (item.value <= 0.0D) {
        throw new IllegalArgumentException(String.format(
            "Item value should be positive: item=%s", item));
      }
      if (item.weight <= 0.0D) {
        throw new IllegalArgumentException(String.format(
            "Item weight should be positive: item=%s", item));
      }
    });
  }

  private static void validateMaxWeight(double maxWeight) {
    if (maxWeight <= 0.0D) {
      throw new IllegalArgumentException(String.format(
          "Max weight should be positive: maxWeight=%s", maxWeight));
    }
  }

  private static void validateMaxNumberOfIterations(int maxNumberOfIterations) {
    if (maxNumberOfIterations < 1) {
      throw new IllegalArgumentException(String.format(
          "Max number of iterations should be positive: maxNumberOfIterations=%s",
          maxNumberOfIterations));
    }
  }

  private static void validateAlphaRate(double alphaRate) {
    if (alphaRate < 0.0D || alphaRate > 1.0D) {
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

  private NaturalSelectionConfig naturalSelectionConfig(
      Knapsack01GeneticAlgorithmSearchRequest request) {
    return new NaturalSelectionConfig(
        request.maxNumberOfIterations,
        request.alphaRate,
        request.omegaRate,
        request.populationSize,
        chromosomeSupplier(request),
        fitnessFunction(request),
        crossoverPairSelectFunction(),
        crossoverFunction(request),
        List.of(mutationFunction(request))
    );
  }

  private List<Item> selectedItems(
      Chromosome chromosome, Knapsack01GeneticAlgorithmSearchRequest request) {
    return IntStream.range(0, request.numberOfItems())
        .filter(itemIndex -> chromosome.hasGenValue(itemIndex, ITEM_SELECTED))
        .mapToObj(request.items::get)
        .toList();
  }

  public ChromosomeSupplier chromosomeSupplier(
      Knapsack01GeneticAlgorithmSearchRequest request) {
    return () -> {
      var gens = IntStream.range(0, request.numberOfItems())
          .mapToDouble(itemIndex -> random.nextBoolean() ? ITEM_SELECTED : ITEM_SKIPPED)
          .toArray();
      return new Chromosome(gens);
    };
  }

  private FitnessFunction fitnessFunction(
      Knapsack01GeneticAlgorithmSearchRequest request) {
    return chromosome -> {
      var knapsackWeight = IntStream.range(0, request.numberOfItems())
          .filter(itemIndex -> chromosome.hasGenValue(itemIndex, ITEM_SELECTED))
          .mapToDouble(itemIndex -> request.items().get(itemIndex).weight())
          .sum();
      Supplier<Double> knapsackValueSup = () -> IntStream.range(0, request.numberOfItems())
          .filter(itemIndex -> chromosome.hasGenValue(itemIndex, ITEM_SELECTED))
          .mapToDouble(itemIndex -> request.items().get(itemIndex).value())
          .sum();
      // if knapsack weight exceeds max weight then such cases are treated as impossible (with zero value)
      return knapsackWeight <= request.maxWeight() ? knapsackValueSup.get() : 0.0D;
    };
  }

  private CrossoverPairSelectFunction crossoverPairSelectFunction() {
    return population -> {
      var randomChromosomeIndex = random.nextInt(0, population.size());
      return population.get(randomChromosomeIndex);
    };
  }

  private CrossoverFunction crossoverFunction(Knapsack01GeneticAlgorithmSearchRequest request) {
    return (leftChromosome, rightChromosome) -> {
      if (random.nextDouble() < request.crossoverProbability()) {
        var splitGenIndex = random.nextInt(0, request.numberOfItems());
        var crossedGens = new double[request.numberOfItems()];
        for (int genIndex = 0; genIndex < request.numberOfItems(); genIndex++) {
          crossedGens[genIndex] = genIndex < splitGenIndex ?
              leftChromosome.gen(genIndex) : rightChromosome.gen(genIndex);
        }
        return new Chromosome(crossedGens);
      } else {
        return random.nextBoolean() ? leftChromosome : rightChromosome;
      }
    };
  }

  private MutationFunction mutationFunction(Knapsack01GeneticAlgorithmSearchRequest request) {
    return chromosome -> {
      var mutatedGens = new double[request.numberOfItems()];
      for (int genIndex = 0; genIndex < request.numberOfItems(); genIndex++) {
        mutatedGens[genIndex] = random.nextDouble() < request.mutationProbability() ?
            1 - chromosome.gen(genIndex) : chromosome.gen(genIndex);
      }
      return new Chromosome(mutatedGens);
    };
  }

  public record Item(
      String name,
      double value,
      double weight) {

  }

  public record Knapsack01GeneticAlgorithmSearchRequest(
      List<Item> items,
      double maxWeight,
      int maxNumberOfIterations,
      int populationSize,
      double alphaRate,
      double omegaRate,
      double crossoverProbability,
      double mutationProbability
  ) {

    public int numberOfItems() {
      return items.size();
    }
  }

  public record Knapsack01GeneticAlgorithmSearchResponse(
      List<Item> items,
      double maxWeight,
      int maxNumberOfIterations,
      int populationSize,
      double alphaRate,
      double omegaRate,
      double crossoverProbability,
      double mutationProbability,
      List<Item> selectedItems
  ) {

    public double totalValue() {
      return selectedItems.stream().mapToDouble(Item::value).sum();
    }

    public double totalWeight() {
      return selectedItems.stream().mapToDouble(Item::weight).sum();
    }

    @Override
    public String toString() {
      return String.format(
          "Knapsack01GeneticAlgorithmSearchResponse[totalValue=%s, totalWeight=%s, selectedItems=%s]",
          totalValue(), totalWeight(), selectedItems);
    }
  }
}