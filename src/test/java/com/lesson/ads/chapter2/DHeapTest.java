package com.lesson.ads.chapter2;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class DHeapTest {

  private final Random random = new Random();

  @Test
  void shouldGetMaxElement() {
    // Given
    final var maxHeap = new DHeap<Integer>(15, 2, Comparator.reverseOrder());
    masterIntList().forEach(maxHeap::add);

    // When
    final var result = maxHeap.pop();

    // Then
    Assertions.assertThat(result).isEqualTo(101);
  }

  @Test
  void shouldGetMinElement() {
    // Given
    final var minHeap = new DHeap<Integer>(15, 2, Comparator.naturalOrder());
    masterIntList().forEach(minHeap::add);

    // When
    final var result = minHeap.pop();

    // Then
    Assertions.assertThat(result).isEqualTo(-20);
  }

  @RepeatedTest(value = 100)
  void shouldGetMinElementRandomDHeap() {
    // Given
    final var size = Math.abs(random.nextInt(1000000));
    final var branchFactor = Math.abs(random.nextInt(8)) + 2;
    final var minHeap = new DHeap<Integer>(size + 1, branchFactor);
    final var randomIntList = randomIntList(size);
    final var min = randomIntList.stream().min(Integer::compareTo);
    randomIntList.forEach(minHeap::add);

    // When
    final var result = minHeap.pop();

    // Then
    Assertions.assertThat(result).isEqualTo(min.orElseThrow());
  }

  private List<Integer> masterIntList() {
    return Stream.of(10, 101, 6, 1, -20, 20, 78, 12, 34, -8, 55, 90, -1, -1, 0).toList();
  }

  private List<Integer> randomIntList(int size) {
    return IntStream.rangeClosed(0, size)
        .mapToObj(index -> random.nextInt())
        .toList();
  }
}