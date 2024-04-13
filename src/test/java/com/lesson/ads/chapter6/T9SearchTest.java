package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.T9Search.T9SearchResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class T9SearchTest {

  @Test
  void shouldSearch() {
    // Given
    // snow
    final int[] pressedButtons = {7, 6, 6, 9};
    final var sut = new T9Search(commonDictionary());

    // When
    final var result = sut.search(pressedButtons);

    // Then
    Assertions.assertThat(result)
        .returns(List.of("snow", "snowman", "snowflake"), T9SearchResult::recommendations);
  }

  @Test
  void shouldSearchCaseEmptyRecommendations() {
    // Given
    // snows
    final int[] pressedButtons = {7, 6, 6, 9, 7};
    final var sut = new T9Search(commonDictionary());

    // When
    final var result = sut.search(pressedButtons);

    // Then
    Assertions.assertThat(result)
        .returns(List.of(), T9SearchResult::recommendations);
  }

  @Test
  void shouldSearchCaseEnglishDictionary() {
    // Given
    // snows
    final int[] pressedButtons = {7, 6, 6, 9, 7, 4, 6};
    final var sut = new T9Search(englishDictionary());

    // When
    final var result = sut.search(pressedButtons);

    // Then
    Assertions.assertThat(result)
        .returns(
            List.of("snowshoe", "snowshoes", "snowshoer", "snowshoed", "snowshoing", "snowshoeing"),
            T9SearchResult::recommendations);
  }

  private List<String> commonDictionary() {
    return List.of("snow", "snowman", "snowflake", "winter");
  }

  @SneakyThrows
  private static List<String> englishDictionary() {
    return Files.readAllLines(Path.of("src/test/resources/english_words.txt"));
  }
}