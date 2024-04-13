package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.AutocompleteSearch.AutocompleteSearchResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AutocompleteSearchTest {

  @Test
  void shouldSearch() {
    // Given
    final var prefix = "snow";
    final var sut = new AutocompleteSearch(commonDictionary());

    // When
    final var result = sut.search(prefix);

    // Then
    Assertions.assertThat(result)
        .returns(prefix, AutocompleteSearchResult::prefix)
        .returns(List.of("snow", "snowman", "snowflake"),
            AutocompleteSearchResult::recommendations);
  }

  @Test
  void shouldSearchCaseEmptyRecommendations() {
    // Given
    final var prefix = "snows";
    final var sut = new AutocompleteSearch(commonDictionary());

    // When
    final var result = sut.search(prefix);

    // Then
    Assertions.assertThat(result)
        .returns(prefix, AutocompleteSearchResult::prefix)
        .returns(List.of(), AutocompleteSearchResult::recommendations);
  }

  @Test
  void shouldSearchCaseEnglishDictionary() {
    // Given
    final var prefix = "snowsho";
    final var sut = new AutocompleteSearch(englishDictionary());

    // When
    final var result = sut.search(prefix);

    // Then
    Assertions.assertThat(result)
        .returns(prefix, AutocompleteSearchResult::prefix)
        .returns(
            List.of("snowshoe", "snowshoes", "snowshoer", "snowshoed", "snowshoing", "snowshoeing"),
            AutocompleteSearchResult::recommendations);
  }

  private List<String> commonDictionary() {
    return List.of("snow", "snowman", "snowflake", "winter");
  }

  @SneakyThrows
  private static List<String> englishDictionary() {
    return Files.readAllLines(Path.of("src/test/resources/english_words.txt"));
  }
}