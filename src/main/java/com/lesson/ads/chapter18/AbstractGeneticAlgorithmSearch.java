package com.lesson.ads.chapter18;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractGeneticAlgorithmSearch {

  Optional<Chromosome> search(NaturalSelectionConfig config) {
    var population = initialPopulation(config);
    for (int iteration = 0; iteration < config.maxNumberOfIterations; iteration++) {
      population = naturalSelection(population, config);
    }
    return population.stream()
        .filter(chromosome -> config.fitnessFunction.apply(chromosome) > 0)
        .max(Comparator.comparingDouble(config.fitnessFunction::apply));
  }

  private List<Chromosome> initialPopulation(NaturalSelectionConfig config) {
    return IntStream.iterate(0, i -> i + 1)
        .mapToObj(index -> config.chromosomeSupplier.supply())
        .filter(chromosome -> config.fitnessFunction.apply(chromosome) > 0)
        .limit(config.populationSize)
        .toList();
  }

  private List<Chromosome> naturalSelection(
      List<Chromosome> population, NaturalSelectionConfig config) {
    // alpha organism (evolution winners)
    var alphas =
        alphas(population, config.alphaRate, config.fitnessFunction);
    // omega organisms (evolution losers)
    var omegaPredicate =
        omegaPredicate(alphas, config.omegaRate, config.fitnessFunction);
    // available for mating organisms
    var successfulPopulation =
        successfulPopulation(population, omegaPredicate.negate());

    var nextPopulation = new ArrayList<>(alphas);
    while (nextPopulation.size() < config.populationSize) {
      var leftChromosome =
          config.crossoverPairSelectFunction.apply(successfulPopulation);
      var rightChromosome =
          config.crossoverPairSelectFunction.apply(successfulPopulation);
      var crossedChromosome =
          config.crossoverFunction.apply(leftChromosome, rightChromosome);
      for (MutationFunction mutationFunction : config.mutationFunctions) {
        crossedChromosome = mutationFunction.apply(crossedChromosome);
      }
      nextPopulation.add(crossedChromosome);
    }
    return nextPopulation;
  }

  private List<Chromosome> alphas(
      List<Chromosome> population, double alphaRate, FitnessFunction fitnessFunction) {
    return population.stream()
        .sorted(Comparator.comparingDouble(fitnessFunction::apply).reversed())
        .limit((long) (alphaRate * population.size()))
        .toList();
  }

  private Predicate<Chromosome> omegaPredicate(
      List<Chromosome> alphas, double omegaRate, FitnessFunction fitnessFunction) {
    return chromosome -> alphas.stream().findFirst()
        .filter(alphaChromosome ->
            fitnessFunction.apply(chromosome) / fitnessFunction.apply(alphaChromosome) < omegaRate)
        .isPresent();
  }

  private List<Chromosome> successfulPopulation(
      List<Chromosome> population, Predicate<Chromosome> successPredicate) {
    return population.stream()
        .filter(successPredicate)
        .collect(Collectors.toList());
  }

  record Chromosome(
      double[] gens
  ) {

    public double gen(int index) {
      return gens[index];
    }

    public boolean hasGenValue(int index, double value) {
      return gens[index] == value;
    }

    public int numberOfGens() {
      return gens.length;
    }
  }

  record NaturalSelectionConfig(
      int maxNumberOfIterations,
      double alphaRate,
      double omegaRate,
      int populationSize,
      ChromosomeSupplier chromosomeSupplier,
      FitnessFunction fitnessFunction,
      CrossoverPairSelectFunction crossoverPairSelectFunction,
      CrossoverFunction crossoverFunction,
      List<MutationFunction> mutationFunctions
  ) {

  }

  @FunctionalInterface
  interface ChromosomeSupplier {

    Chromosome supply();
  }

  @FunctionalInterface
  interface FitnessFunction {

    double apply(Chromosome chromosome);
  }

  @FunctionalInterface
  interface CrossoverPairSelectFunction {

    Chromosome apply(List<Chromosome> population);
  }

  @FunctionalInterface
  interface CrossoverFunction {

    Chromosome apply(Chromosome leftChromosome, Chromosome rightChromosome);
  }

  @FunctionalInterface
  interface MutationFunction {

    Chromosome apply(Chromosome chromosome);
  }
}