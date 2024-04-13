package com.lesson.ads.chapter18;

import com.lesson.ads.chapter18.TspGeneticAlgorithmSearch.TspGeneticAlgorithmSearchRequest;
import com.lesson.ads.chapter18.TspGeneticAlgorithmSearch.TspGeneticAlgorithmSearchResponse;
import com.lesson.ads.util.Point;
import java.util.ArrayList;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("MonteCarlo")
class TspGeneticAlgorithmSearchTest {

  private TspGeneticAlgorithmSearch sut;

  @BeforeEach
  void beforeEach() {
    sut = new TspGeneticAlgorithmSearch();
  }

  @Test
  void shouldSearchCaseFourCities() {
    // Given
    final var numberOfCities = 4;
    final double[][] intercityRoadLengths = {
        {0, 10, 15, 20},
        {10, 0, 35, 25},
        {15, 35, 0, 30},
        {20, 25, 30, 0}
    };
    final var request = new TspGeneticAlgorithmSearchRequest(
        numberOfCities,
        intercityRoadLengths,
        1000,
        1000,
        0.2D,
        0.0D,
        0.3D,
        0.7D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspGeneticAlgorithmSearchResponse::numberOfCities)
        .returns(intercityRoadLengths, TspGeneticAlgorithmSearchResponse::intercityRoadLengths)
        .returns(request.maxNumberOfIterations(),
            TspGeneticAlgorithmSearchResponse::maxNumberOfIterations)
        .returns(request.populationSize(), TspGeneticAlgorithmSearchResponse::populationSize)
        .returns(request.alphaRate(), TspGeneticAlgorithmSearchResponse::alphaRate)
        .returns(request.omegaRate(), TspGeneticAlgorithmSearchResponse::omegaRate)
        .returns(request.crossoverProbability(),
            TspGeneticAlgorithmSearchResponse::crossoverProbability)
        .returns(request.mutationProbability(),
            TspGeneticAlgorithmSearchResponse::mutationProbability)
        .returns(80.0D, TspGeneticAlgorithmSearchResponse::tourRoadLength);
  }

  @Test
  void shouldSearchCaseSevenCities() {
    final var numberOfCities = 20;
    final var cityCoordinates = new ArrayList<Point>();
    IntStream.range(0, numberOfCities / 2).forEach(index ->
        cityCoordinates.add(new Point(index, 10)));
    IntStream.range(0, numberOfCities / 2).forEach(index ->
        cityCoordinates.add(new Point(index, 0)));
    final double[][] intercityRoadLengths = new double[numberOfCities][numberOfCities];
    IntStream.range(0, numberOfCities).forEach(row ->
        IntStream.range(0, numberOfCities).forEach(column ->
            intercityRoadLengths[row][column] =
                cityCoordinates.get(row).distance(cityCoordinates.get(column))));
    final var request = new TspGeneticAlgorithmSearchRequest(
        numberOfCities,
        intercityRoadLengths,
        1000,
        1000,
        0.2D,
        0.0D,
        0.3D,
        0.7D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspGeneticAlgorithmSearchResponse::numberOfCities)
        .returns(intercityRoadLengths, TspGeneticAlgorithmSearchResponse::intercityRoadLengths)
        .returns(request.maxNumberOfIterations(),
            TspGeneticAlgorithmSearchResponse::maxNumberOfIterations)
        .returns(request.populationSize(), TspGeneticAlgorithmSearchResponse::populationSize)
        .returns(request.alphaRate(), TspGeneticAlgorithmSearchResponse::alphaRate)
        .returns(request.omegaRate(), TspGeneticAlgorithmSearchResponse::omegaRate)
        .returns(request.crossoverProbability(),
            TspGeneticAlgorithmSearchResponse::crossoverProbability)
        .returns(request.mutationProbability(),
            TspGeneticAlgorithmSearchResponse::mutationProbability)
        .returns(38.0D, TspGeneticAlgorithmSearchResponse::tourRoadLength);
  }
}