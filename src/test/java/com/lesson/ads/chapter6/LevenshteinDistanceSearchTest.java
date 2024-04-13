package com.lesson.ads.chapter6;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LevenshteinDistanceSearchTest {

  @ParameterizedTest
  @MethodSource("argumentsStream")
  void shouldRecursiveSearch(String left, String right, int expectedDistance) {
    // When
    final var result = LevenshteinDistanceSearch.recursiveSearch(left, right);

    //Then
    Assertions.assertThat(result).isEqualTo(expectedDistance);
  }

  @ParameterizedTest
  @MethodSource("argumentsStream")
  void shouldDpSearch(String left, String right, int expectedDistance) {
    // When
    final var result = LevenshteinDistanceSearch.dpSearch(left, right);

    //Then
    Assertions.assertThat(result).isEqualTo(expectedDistance);
  }

  private static Stream<Arguments> argumentsStream() {
    return Stream.of(
        Arguments.of("aaa", "", 3),
        Arguments.of("", "bbb", 3),
        Arguments.of("aaa", "aab", 1),
        Arguments.of("aaa", "bbb", 3),
        Arguments.of("aa", "bbb", 3),
        Arguments.of("polynomial", "exponential", 6)
    );
  }
}