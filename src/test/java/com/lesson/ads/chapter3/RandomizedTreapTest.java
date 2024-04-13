package com.lesson.ads.chapter3;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomizedTreapTest {

  @Test
  void shouldGet() {
    // Given
    final var key = "K5";
    final var sut = masterRandomizedTreap();

    // When
    final var result = sut.get(key);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
  }

  @Test
  void shouldGetMin() {
    // Given
    final var sut = masterRandomizedTreap();

    // When
    final var result = sut.min();

    // Then
    Assertions.assertThat(result).get().isEqualTo("K1");
  }

  @Test
  void shouldGetMax() {
    // Given
    final var sut = masterRandomizedTreap();

    // When
    final var result = sut.max();

    // Then
    Assertions.assertThat(result).get().isEqualTo("K9");
  }

  @Test
  void shouldDelete() {
    // Given
    final var key = "K5";
    final var sut = masterRandomizedTreap();

    // When
    final var result = sut.delete(key);

    // Then
    Assertions.assertThat(result).isEqualTo("K5");
  }

  private RandomizedTreap<String> masterRandomizedTreap() {
    var randomizedTreap = new RandomizedTreap<String>();
    randomizedTreap.put("K1");
    randomizedTreap.put("K2");
    randomizedTreap.put("K3");
    randomizedTreap.put("K4");
    randomizedTreap.put("K5");
    randomizedTreap.put("K6");
    randomizedTreap.put("K7");
    randomizedTreap.put("K8");
    randomizedTreap.put("K9");
    return randomizedTreap;
  }
}