package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.SpellCheckSearch.SpellCheckSearchResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SpellCheckSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var word = "snowman";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(true, SpellCheckSearchResult::valid)
        .returns(List.of(), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchCaseEmptyRecommendations() {
    // Given
    final var word = "sun";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(false, SpellCheckSearchResult::valid)
        .returns(List.of(), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistance() {
    // Given
    final var word = "snowman";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word, 0);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(true, SpellCheckSearchResult::valid)
        .returns(List.of(), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistanceEmptyRecommendations() {
    // Given
    final var word = "snowmn";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word, 0);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(false, SpellCheckSearchResult::valid)
        .returns(List.of(), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistanceDeleteSymbol() {
    // Given
    final var word = "snowmans";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word, 1);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(false, SpellCheckSearchResult::valid)
        .returns(List.of("snowman"), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistanceSubstituteSymbol() {
    // Given
    final var word = "snouman";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word, 1);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(false, SpellCheckSearchResult::valid)
        .returns(List.of("snowman"), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistanceInsertSymbols() {
    // Given
    final var word = "snow";
    final var sut = new SpellCheckSearch(commonDictionary());

    // When
    final var result = sut.search(word, 3);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(true, SpellCheckSearchResult::valid)
        .returns(List.of("snowman"), SpellCheckSearchResult::recommendations);
  }

  @Test
  void shouldSearchWithLevenshteinDistanceEnglishDictionary() {
    // Given
    final var word = "snowmn";
    final var sut = new SpellCheckSearch(englishDictionary());

    // When
    final var result = sut.search(word, 1);

    // Then
    Assertions.assertThat(result)
        .returns(word, SpellCheckSearchResult::word)
        .returns(false, SpellCheckSearchResult::valid)
        .returns(List.of("snowman", "snowmen"), SpellCheckSearchResult::recommendations);
  }

  private List<String> commonDictionary() {
    return List.of("snow", "snowman", "snowflake", "winter");
  }

  @SneakyThrows
  private static List<String> englishDictionary() {
    return Files.readAllLines(Path.of("src/test/resources/english_words.txt"));
  }
}