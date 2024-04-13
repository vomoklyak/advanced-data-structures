package com.lesson.ads.chapter17;

import static com.lesson.ads.chapter17.TspBruteForceSearch.NONE;

import com.lesson.ads.chapter17.TspBruteForceSearch.TspBruteForceSearchRequest;
import com.lesson.ads.chapter17.TspBruteForceSearch.TspBruteForceSearchResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TspBruteForceSearchTest {

  @Test
  void shouldSearchCaseFourCities() {
    // Given
    final var numberOfCities = 4;
    final double[][] intercityRoadLengths = {
        {0.0D, 10.0D, 15.0D, 20.0D},
        {10.0D, 0.0D, 35.0D, 25.0D},
        {15.0D, 35.0D, 0.0D, 30.0D},
        {20.0D, 25.0D, 30.0D, 0.0D}
    };
    final var request = new TspBruteForceSearchRequest(numberOfCities, intercityRoadLengths);

    // When
    final var result = TspBruteForceSearch.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspBruteForceSearchResponse::numberOfCities)
        .returns(intercityRoadLengths, TspBruteForceSearchResponse::intercityRoadLengths)
        .returns(new int[]{0, 1, 3, 2, 0}, TspBruteForceSearchResponse::tour)
        .returns(80.0D, TspBruteForceSearchResponse::tourRoadLength);
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
    final var request = new TspBruteForceSearchRequest(numberOfCities, intercityRoadLengths);

    // When
    final var result = TspBruteForceSearch.search(request);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfCities, TspBruteForceSearchResponse::numberOfCities)
        .returns(intercityRoadLengths, TspBruteForceSearchResponse::intercityRoadLengths)
        .returns(new int[]{0, 6, 3, 5, 4, 2, 1, 0}, TspBruteForceSearchResponse::tour)
        .returns(51.0D, TspBruteForceSearchResponse::tourRoadLength);
  }
}