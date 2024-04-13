package com.lesson.ads.chapter4;

import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BloomFilterTest {

  @Test
  void shouldCheckContains() {
    // Given
    final var key = "key".getBytes(StandardCharsets.UTF_8);
    final var sut =
        BloomFilter.create(100, 3, 0);
    sut.add(key);

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckContainsCaseAnotherKey() {
    // Given
    final var key = "key".getBytes(StandardCharsets.UTF_8);
    final var anotherKey = "another key".getBytes(StandardCharsets.UTF_8);
    final var sut =
        BloomFilter.create(100, 3, 0);
    sut.add(anotherKey);

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldCreate() {
    // When
    final var numberOfBits = 100;
    final var numberOfBitHashFunctions = 3;
    final var result =
        BloomFilter.create(numberOfBits, numberOfBitHashFunctions, 0);

    // Then
    Assertions.assertThat(result)
        .returns(numberOfBits, BloomFilter::numberOfBits)
        .returns(numberOfBitHashFunctions, BloomFilter::numberOfBitHashFunctions);
  }

  @Test
  void shouldApproximate() {
    // When
    final var result =
        BloomFilter.approximate(0, 0.9D, 100);

    // Then
    Assertions.assertThat(result)
        .returns(479, BloomFilter::numberOfBits)
        .returns(3, BloomFilter::numberOfBitHashFunctions);
  }
}