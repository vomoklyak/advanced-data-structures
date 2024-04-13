package com.lesson.ads.chapter18;

import com.lesson.ads.chapter18.Knapsack01GeneticAlgorithmSearch.Item;
import com.lesson.ads.chapter18.Knapsack01GeneticAlgorithmSearch.Knapsack01GeneticAlgorithmSearchRequest;
import com.lesson.ads.chapter18.Knapsack01GeneticAlgorithmSearch.Knapsack01GeneticAlgorithmSearchResponse;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("MonteCarlo")
class Knapsack01GeneticAlgorithmSearchTest {

  private Knapsack01GeneticAlgorithmSearch sut;


  @BeforeEach
  void beforeEach() {
    sut = new Knapsack01GeneticAlgorithmSearch();
  }

  @Test
  void shouldSearchCaseFourCities() {
    // Given
    final var maxWeight = 15.0D;
    final var items = List.of(
        new Item("gold", 10.0D, 1.0D),
        new Item("gold", 10.0D, 1.0D),
        new Item("gold", 10.0D, 1.0D),
        new Item("silver", 5.0D, 2.0D),
        new Item("silver", 5.0D, 2.0D),
        new Item("silver", 5.0D, 2.0D),
        new Item("sapphire", 40.0D, 1.0D),
        new Item("bronze", 1.0D, 3.0D),
        new Item("bronze", 1.0D, 3.0D),
        new Item("bronze", 1.0D, 3.0D),
        new Item("platinum", 25.0D, 2.0D),
        new Item("platinum", 25.0D, 2.0D),
        new Item("platinum", 12.5D, 1.0D),
        new Item("iron", 1.0D, 5.0D),
        new Item("iron", 1.0D, 5.0D),
        new Item("iron", 1.0D, 5.0D),
        new Item("copper", 1.0D, 4.0D),
        new Item("copper", 1.0D, 4.0D),
        new Item("ruby", 30.0D, 1.0D),
        new Item("copper", 1.0D, 4.0D),
        new Item("aluminum", 1.0D, 3.0D),
        new Item("aluminum", 1.0D, 3.0D),
        new Item("aluminum", 1.0D, 3.0D),
        new Item("diamond", 50.0D, 1.0D)
    );
    final var request = new Knapsack01GeneticAlgorithmSearchRequest(
        items,
        maxWeight,
        1000,
        100,
        0.2D,
        0.2D,
        0.9D,
        0.01D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(request.items(), Knapsack01GeneticAlgorithmSearchResponse::items)
        .returns(request.maxWeight(), Knapsack01GeneticAlgorithmSearchResponse::maxWeight)
        .returns(request.maxNumberOfIterations(),
            Knapsack01GeneticAlgorithmSearchResponse::maxNumberOfIterations)
        .returns(request.populationSize(), Knapsack01GeneticAlgorithmSearchResponse::populationSize)
        .returns(request.alphaRate(), Knapsack01GeneticAlgorithmSearchResponse::alphaRate)
        .returns(request.omegaRate(), Knapsack01GeneticAlgorithmSearchResponse::omegaRate)
        .returns(request.crossoverProbability(),
            Knapsack01GeneticAlgorithmSearchResponse::crossoverProbability)
        .returns(request.mutationProbability(),
            Knapsack01GeneticAlgorithmSearchResponse::mutationProbability)
        .returns(222.5D, Knapsack01GeneticAlgorithmSearchResponse::totalValue)
        .returns(15.0D, Knapsack01GeneticAlgorithmSearchResponse::totalWeight);
  }
}