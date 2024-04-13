package com.lesson.ads.chapter2;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HuffmanCodingTest {

  @Test
  void shouldEncode() {
    // Given
    final var text = "lossless";

    // When
    final var result = HuffmanCoding.encode(text);

    // Then
    Assertions.assertThat(result).isEqualTo("01001110100011");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void shouldEncodeCaseNullText() {
    // Given
    final var text = (String) null;

    // When
    final var result = HuffmanCoding.encode(text);

    // Then
    Assertions.assertThat(result).isNull();
  }

  @Test
  void shouldEncodeCaseEmptyText() {
    // Given
    final var text = "";

    // When
    final var result = HuffmanCoding.encode(text);

    // Then
    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  void shouldEncodeCaseOneLetterText() {
    // Given
    final var text = "l";

    // When
    final var result = HuffmanCoding.encode(text);

    // Then
    Assertions.assertThat(result).isEqualTo("1");
  }
}