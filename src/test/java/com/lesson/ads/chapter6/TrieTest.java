package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.Trie.TrieNode;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TrieTest {

  @Test
  void shouldPut() {
    // Given
    final var key = "a";
    final var sut = Trie.create();

    // When
    final var result = sut.put(key);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
  }

  @Test
  void shouldDelete() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of(key));

    // When
    final var result = sut.delete(key);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
  }

  @Test
  void shouldDeleteCaseEmptyKey() {
    // Given
    final var key = "";
    final var sut = Trie.create(Set.of("a"));

    // When
    sut.delete(key);
    final var result = sut.contains(key);

    // Then
    // not possible to delete root node
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldDeleteCaseNonExistentKey() {
    // Given
    final var key = "a";
    final var sut = Trie.create();

    // When
    final var result = sut.delete(key);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteCaseDanglingNode() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of(key));

    // When
    sut.delete(key);
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldDeleteCaseNonDanglingNode() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of(key, "ab"));

    // When
    sut.delete(key);
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldCheckContains() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of(key));

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckContainsCaseEmptyKey() {
    // Given
    final var key = "";
    final var sut = Trie.create();

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckContainsCaseNonExistentKey() {
    // Given
    final var key = "a";
    final var sut = Trie.create();

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldGetLongestPrefixCaseFullPrefix() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of("ab"));

    // When
    final var result = sut.longestPrefix(key);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
  }

  @Test
  void shouldGetLongestPrefixCasePartialPrefix() {
    // Given
    final var keyOne = "a";
    final var keyTwo = "ab";
    final var sut = Trie.create(Set.of(keyOne));

    // When
    final var result = sut.longestPrefix(keyTwo);

    // Then
    Assertions.assertThat(result).isEqualTo(keyOne);
  }

  @Test
  void shouldGetLongestPrefixCaseEmptyPrefix() {
    // Given
    final var key = "a";
    final var sut = Trie.create(Set.of("b"));

    // When
    final var result = sut.longestPrefix(key);

    // Then
    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  void shouldGetKeys() {
    // Given
    final var prefix = "a";
    final var keyOne = "a";
    final var keyTwo = "ab";
    final var sut = Trie.create(Set.of(keyOne, keyTwo, "c"));

    // When
    final var result = sut.keys(prefix);

    // Then
    Assertions.assertThat(result).containsOnly(keyOne, keyTwo);
  }

  @Test
  void shouldGetKeysCaseEmptyPrefixKeys() {
    // Given
    final var prefix = "c";
    final var keyOne = "a";
    final var keyTwo = "ab";
    final var sut = Trie.create(Set.of(keyOne, keyTwo));

    // When
    final var result = sut.keys(prefix);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetKeysCasePrefixKeys() {
    // Given
    final var prefix = "";
    final var keyOne = "a";
    final var keyTwo = "ab";
    final var sut = Trie.create(Set.of(keyOne, keyTwo));

    // When
    final var result = sut.keys(prefix);

    // Then
    Assertions.assertThat(result).containsOnly(prefix, keyOne, keyTwo);
  }

  @Test
  void shouldCheckEmptyCaseEmpty() {
    // Given
    final var sut = Trie.create();

    // When
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckEmptyCaseNonEmpty() {
    // Given
    final var sut = Trie.create(Set.of("a"));

    // When
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldAccept() {
    // Given
    final var keyOne = "a";
    final var keyTwo = "abc";
    final var sut = Trie.create(Set.of(keyOne, keyTwo));
    final var nodeCharacters = new ArrayList<Character>();
    final var nodeIntermediates = new ArrayList<Boolean>();
    final Consumer<TrieNode> trieProcessor = root -> root.getCharToChild().forEach(
        (character, node) -> {
          nodeCharacters.add(character);
          nodeIntermediates.add(node.isIntermediate());
          node.getCharToChild().forEach((childNodeCharacter, childNode) ->
              accept(childNodeCharacter, childNode, nodeCharacters, nodeIntermediates));
        });

    // When
    sut.accept(trieProcessor);

    // Then
    Assertions.assertThat(nodeCharacters).containsOnly('a', 'b', 'c');
    Assertions.assertThat(nodeIntermediates).containsOnly(false, true, false);
  }

  private void accept(Character character, TrieNode node,
      ArrayList<Character> nodeCharacters, ArrayList<Boolean> nodeIntermediates) {
    nodeCharacters.add(character);
    nodeIntermediates.add(node.isIntermediate());
    node.getCharToChild().forEach((childNodeCharacter, childNode) ->
        accept(childNodeCharacter, childNode, nodeCharacters, nodeIntermediates));
  }
}