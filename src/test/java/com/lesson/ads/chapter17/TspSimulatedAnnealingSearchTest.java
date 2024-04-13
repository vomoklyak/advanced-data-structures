package com.lesson.ads.chapter17;

import static com.lesson.ads.chapter17.TspSimulatedAnnealingSearch.NONE;

import com.lesson.ads.chapter17.TspSimulatedAnnealingSearch.TspSimulatedAnnealingRequest;
import com.lesson.ads.chapter17.TspSimulatedAnnealingSearch.TspSimulatedAnnealingResponse;
import com.lesson.ads.util.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("MonteCarlo")
class TspSimulatedAnnealingSearchTest {

  private TspSimulatedAnnealingSearch sut;

  @BeforeEach
  void beforeEach() {
    sut = new TspSimulatedAnnealingSearch();
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
    final var request = new TspSimulatedAnnealingRequest(
        numberOfCities,
        intercityRoadLengths,
        10000,
        100.0D,
        1.0D,
        0.99D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspSimulatedAnnealingResponse::numberOfCities)
        .returns(intercityRoadLengths, TspSimulatedAnnealingResponse::intercityRoadLengths)
        .returns(request.maxNumberOfIterations(),
            TspSimulatedAnnealingResponse::maxNumberOfIterations)
        .returns(request.temperature(), TspSimulatedAnnealingResponse::temperature)
        .returns(request.alphaRate(), TspSimulatedAnnealingResponse::alphaRate)
        .returns(request.betaRate(), TspSimulatedAnnealingResponse::betaRate)
        .returns(80.0D, TspSimulatedAnnealingResponse::tourRoadLength);
  }

  @Test
  void shouldSearchCaseSevenCities() {
    // Given
    final var numberOfCities = 7;
    final double[][] intercityRoadLengths = {
        {0.0D, 12.0D, 10.0D, NONE, NONE, NONE, 12.0D},
        {12.0D, 0.0D, 8.0D, 12.0D, NONE, NONE, NONE},
        {10.0D, 8.0D, 0.0D, 11.0D, 3.0D, NONE, 9.0D},
        {NONE, 12.0D, 11.0D, 0.0D, 11.0D, 10.0D, NONE},
        {NONE, NONE, 3.0D, 11.0D, 0.0D, 6.0D, 7.0D},
        {NONE, NONE, NONE, 10.0D, 6.0D, 0.0D, 9.0D},
        {12.0D, NONE, 9.0D, 0.0D, 7.0D, 9.0D, 0.0D}
    };
    final var request = new TspSimulatedAnnealingRequest(
        numberOfCities,
        intercityRoadLengths,
        10000,
        100.0D,
        1.0D,
        0.99D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspSimulatedAnnealingResponse::numberOfCities)
        .returns(intercityRoadLengths, TspSimulatedAnnealingResponse::intercityRoadLengths)
        .returns(request.maxNumberOfIterations(),
            TspSimulatedAnnealingResponse::maxNumberOfIterations)
        .returns(request.temperature(), TspSimulatedAnnealingResponse::temperature)
        .returns(request.alphaRate(), TspSimulatedAnnealingResponse::alphaRate)
        .returns(request.betaRate(), TspSimulatedAnnealingResponse::betaRate)
        .returns(51.0D, TspSimulatedAnnealingResponse::tourRoadLength);
  }

  /**
   * Best tour shape (rectangle, min road length: n - 2 + 20):
   * <br>  - 1 - - - - - - - - -
   * <br>  - 10
   * <br>  - 1 - - - - - - - - -
   */
  @Test
  void shouldSearchCaseTwentyCities() {
    // Given
    final var numberOfCities = 20;
    final var cityCoordinates = new ArrayList<Point>();
    IntStream.range(0, numberOfCities / 2).forEach(index ->
        cityCoordinates.add(new Point(index, 10)));
    IntStream.range(0, numberOfCities / 2).forEach(index ->
        cityCoordinates.add(new Point(index, 0)));
    Collections.shuffle(cityCoordinates);
    final double[][] intercityRoadLengths = new double[numberOfCities][numberOfCities];
    IntStream.range(0, numberOfCities).forEach(row ->
        IntStream.range(0, numberOfCities).forEach(column ->
            intercityRoadLengths[row][column] =
                cityCoordinates.get(row).distance(cityCoordinates.get(column))));
    final var request = new TspSimulatedAnnealingRequest(
        numberOfCities,
        intercityRoadLengths,
        500000000,
        100.0D,
        0.99D,
        0.99D
    );

    // When
    final var result = sut.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspSimulatedAnnealingResponse::numberOfCities)
        .returns(intercityRoadLengths, TspSimulatedAnnealingResponse::intercityRoadLengths)
        .returns(request.maxNumberOfIterations(),
            TspSimulatedAnnealingResponse::maxNumberOfIterations)
        .returns(request.temperature(), TspSimulatedAnnealingResponse::temperature)
        .returns(request.alphaRate(), TspSimulatedAnnealingResponse::alphaRate)
        .returns(request.betaRate(), TspSimulatedAnnealingResponse::betaRate)
        .returns(38.0D, TspSimulatedAnnealingResponse::tourRoadLength);
  }
}