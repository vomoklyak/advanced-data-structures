package com.lesson.ads.chapter5;

import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DisjointSetsTest {

  @Test
  void shouldCreate() {
    // When
    final var sut = new DisjointSets<>();

    // Then
    Assertions.assertThat(sut.disjointSets()).isEmpty();
  }

  @Test
  void shouldCreateWithElements() {
    // Given
    final var elementA = "A";
    final var elementB = "B";

    // When
    final var sut = new DisjointSets<>(Set.of(elementA, elementB));

    // Then
    Assertions.assertThat(sut.disjointSets())
        .containsOnly(Set.of(elementA), Set.of(elementB));
  }

  @Test
  void shouldAdd() {
    // Given
    final var elementA = "A";
    final var elementB = "B";
    final var sut = new DisjointSets<>(Set.of(elementB));

    // When
    final var result = sut.add(elementA);

    // Then
    Assertions.assertThat(result).isEqualTo("A");
    Assertions.assertThat(sut.disjointSets())
        .containsOnly(Set.of(elementA), Set.of(elementB));
  }

  @Test
  void shouldFindRoot() {
    // Given
    final var elementA = "A";
    final var elementB = "B";
    final var sut = new DisjointSets<>(Set.of(elementA, elementB));

    // When
    final var result = sut.findRoot(elementA);

    // Then
    Assertions.assertThat(result).isEqualTo(elementA);
  }

  @Test
  void shouldMerge() {
    // Given
    final var elementA = "A";
    final var elementB = "B";
    final var sut = new DisjointSets<>(Set.of(elementA, elementB));

    // When
    final var result = sut.merge(elementA, elementB);

    // Then
    Assertions.assertThat(result).isTrue();
    Assertions.assertThat(sut.findRoot(elementA)).isEqualTo(elementA);
    Assertions.assertThat(sut.findRoot(elementB)).isEqualTo(elementA);
  }

  @Test
  void shouldMergeCaseAlreadyMerged() {
    // Given
    final var elementA = "A";
    final var elementB = "B";
    final var sut = new DisjointSets<>(Set.of(elementA, elementB));
    sut.merge(elementA, elementB);

    // When
    final var result = sut.merge(elementA, elementB);

    // Then
    Assertions.assertThat(result).isFalse();
    Assertions.assertThat(sut.findRoot(elementA)).isEqualTo(elementA);
    Assertions.assertThat(sut.findRoot(elementB)).isEqualTo(elementA);
  }

  @Test
  void shouldCreateAddMergeFind() {
    // Given
    final var elementA = "A";
    final var elementB = "B";
    final var elementC = "C";
    final var elementD = "D";
    final var elementE = "E";
    final var elementF = "F";
    final var elementG = "G";
    final var elementK = "K";
    final var elementL = "L";
    final var sut = new DisjointSets<>(Set.of(elementA,
        elementB, elementC, elementD, elementE, elementF, elementG, elementK));
    sut.add(elementL);
    sut.merge(elementA, elementB);
    sut.merge(elementB, elementC);
    sut.merge(elementD, elementE);
    sut.merge(elementD, elementF);
    sut.merge(elementD, elementG);
    sut.merge(elementA, elementD);
    sut.merge(elementK, elementL);

    // When
    final var result = sut.disjointSets();

    // Then
    Assertions.assertThat(result)
        .containsOnly(
            Set.of(elementA, elementB, elementC, elementD, elementE, elementF, elementG),
            Set.of(elementK, elementL)
        );
  }
}