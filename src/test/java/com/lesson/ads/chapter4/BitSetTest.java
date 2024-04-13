package com.lesson.ads.chapter4;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BitSetTest {

  @Test
  void shouldSetBit() {
    // Given
    final var bitIndexOne = 63;
    final var sut = new BitSet(128);

    // When
    sut.set(63);

    // Then
    Assertions.assertThat(sut)
        .returns(true, bitSet -> bitSet.get(bitIndexOne));
  }

  @Test
  void shouldUnsetBit() {
    // Given
    final var bitIndexOne = 63;
    final var sut = new BitSet(128);
    sut.set(63);

    // When
    sut.unset(63);

    // Then
    Assertions.assertThat(sut)
        .returns(false, bitSet -> bitSet.get(bitIndexOne));
  }

  @Test
  void shouldGetBit() {
    // Given
    final var bitIndexOne = 63;
    final var sut = new BitSet(128);
    sut.set(bitIndexOne);

    // When
    final var result = sut.get(bitIndexOne);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldGetBitCaseFalse() {
    // Given
    final var bitIndexOne = 63;
    final var sut = new BitSet(128);

    // When
    final var result = sut.get(bitIndexOne);

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldGetSize() {
    // Given
    final var sut = new BitSet(128);

    // When
    final var result = sut.size();

    // Then
    Assertions.assertThat(result).isEqualTo(128);
  }

  @Test
  void shouldGetWords() {
    // Given
    final var sut = new BitSet(128);
    sut.set(0);
    sut.set(63);
    sut.set(64);
    sut.set(65);

    // When
    final var result = sut.words();

    // Then
    Assertions.assertThat(result)
        .containsExactly(-9223372036854775807L, 3L);
  }

  @Test
  void shouldGetBitStr() {
    // Given
    final var sut = new BitSet(128);
    sut.set(0);
    sut.set(63);
    sut.set(64);
    sut.set(65);

    // When
    final var result = sut.bitStr();

    // Then
    Assertions.assertThat(result)
        .isEqualTo("1000000000000000000000000000000000000000000000000000000000000001\n"
            + "1100000000000000000000000000000000000000000000000000000000000000");
  }
}