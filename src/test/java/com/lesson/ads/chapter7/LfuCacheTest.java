package com.lesson.ads.chapter7;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LfuCacheTest {

  @Test
  void shouldPut() {
    // Given
    final var key = "1";
    final var value = 1;
    final var sut = new LfuCache<String, Integer>(5);

    // When
    final var result = sut.put(key, value);

    //Then
    Assertions.assertThat(result).isEqualTo(value);
    Assertions.assertThat(sut.get(key)).get().isEqualTo(value);
  }

  @Test
  void shouldPutCaseUpdate() {
    // Given
    final var key = "1";
    final var valueOne = 1;
    final var valueTwo = 2;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(key, valueOne);

    // When
    final var result = sut.put(key, valueTwo);

    //Then
    Assertions.assertThat(result).isEqualTo(valueTwo);
    Assertions.assertThat(sut.get(key)).get().isEqualTo(valueTwo);
  }

  @Test
  void shouldPutCaseEviction() {
    // Given
    final var keyOne = "1";
    final var keyTwo = "2";
    final var valueOne = 1;
    final var valueTwo = 2;
    final var sut = new LfuCache<String, Integer>(1);
    sut.put(keyOne, valueOne);

    // When
    final var result = sut.put(keyTwo, valueTwo);

    //Then
    Assertions.assertThat(result).isEqualTo(valueTwo);
    Assertions.assertThat(sut.get(keyOne)).isEmpty();
    Assertions.assertThat(sut.get(keyTwo)).get().isEqualTo(valueTwo);
  }

  @Test
  void shouldGet() {
    // Given
    final var keyOne = "1";
    final var valueOne = 1;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(keyOne, valueOne);

    // When
    final var result = sut.get(keyOne);

    //Then
    Assertions.assertThat(result).get().isEqualTo(valueOne);
  }

  @Test
  void shouldGetCaseNonExistentKey() {
    // Given
    final var keyOne = "1";
    final var keyTwo = "2";
    final var valueOne = 1;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(keyOne, valueOne);

    // When
    final var result = sut.get(keyTwo);

    //Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldDelete() {
    // Given
    final var keyOne = "1";
    final var keyTwo = "2";
    final var keyThree = "3";
    final var valueOne = 1;
    final var valueTwo = 2;
    final var valueThree = 3;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(keyOne, valueOne);
    sut.put(keyTwo, valueTwo);
    sut.put(keyThree, valueThree);

    // When
    final var result = sut.delete(keyTwo);

    //Then
    Assertions.assertThat(result).get().isEqualTo(valueTwo);
    Assertions.assertThat(sut.get(keyTwo)).isEmpty();
  }

  @Test
  void shouldDeleteCaseNonExistentKey() {
    // Given
    final var keyOne = "1";
    final var keyTwo = "2";
    final var valueOne = 1;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(keyOne, valueOne);

    // When
    final var result = sut.delete(keyTwo);

    //Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetSize() {
    // Given
    final var keyOne = "1";
    final var valueOne = 1;
    final var sut = new LfuCache<String, Integer>(5);
    sut.put(keyOne, valueOne);

    // When
    final var result = sut.size();

    //Then
    Assertions.assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldGetMaxSize() {
    // Given
    final var sut = new LfuCache<String, Integer>(5);

    // When
    final var result = sut.maxSize();

    //Then
    Assertions.assertThat(result).isEqualTo(5);
  }

  @Test
  void shouldPutGetDelete() {
    // Given
    final var keyOne = "1";
    final var keyTwo = "2";
    final var keyThree = "3";
    final var keyFour = "4";
    final var keyFive = "5";
    final var keySix = "6";
    final var keySeven = "7";
    final var valueOne = 1;
    final var valueTwo = 2;
    final var valueThree = 3;
    final var valueFour = 4;
    final var valueFive = 5;
    final var valueSix = 6;
    final var valueSeven = 6;
    final var sut = new LfuCache<String, Integer>(5);

    // When
    sut.put(keyOne, valueOne);
    sut.put(keyTwo, valueTwo);
    sut.put(keyThree, valueThree);
    sut.put(keyFour, valueFour);
    sut.put(keyFive, valueFive);

    sut.get(keyOne);
    sut.get(keyTwo);
    sut.get(keyThree);

    sut.delete(keyFive);

    sut.put(keySix, valueSix);
    sut.put(keySeven, valueSeven);

    //Then
    Assertions.assertThat(sut.get(keyOne)).get().isEqualTo(valueOne);
    Assertions.assertThat(sut.get(keyTwo)).get().isEqualTo(valueTwo);
    Assertions.assertThat(sut.get(keyThree)).get().isEqualTo(valueThree);
    // evicted
    Assertions.assertThat(sut.get(keyFour)).isEmpty();
    // deleted
    Assertions.assertThat(sut.get(keyFive)).isEmpty();
    Assertions.assertThat(sut.get(keySix)).get().isEqualTo(valueSix);
    Assertions.assertThat(sut.get(keySeven)).get().isEqualTo(valueSeven);
  }
}