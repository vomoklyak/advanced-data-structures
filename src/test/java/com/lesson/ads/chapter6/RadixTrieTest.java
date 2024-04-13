package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.RadixTrie.RadixTrieNode;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RadixTrieTest {

  @Test
  void shouldPut() {
    // Given
    final var key = "abc";
    final var sut = RadixTrie.create(Set.of("ab", "ac"));

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b", "c", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, true, false, false, false);
  }

  @Test
  void shouldPutNullChild() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create();

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false);
  }

  @Test
  void shouldPutCaseZeroSuffixes() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of("ab", "ac"));

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false, false, false);
  }

  @Test
  void shouldPutCaseZeroKeySuffix() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of("aab", "aac"));

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "a", "b", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false, true, false, false);
  }

  @Test
  void shouldPutCaseZeroChildSuffix() {
    // Given
    final var key = "ab";
    final var sut = RadixTrie.create(Set.of("a"));

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false, false);
  }

  @Test
  void shouldPutCaseNonZeroSuffixes() {
    // Given
    final var key = "ab";
    final var sut = RadixTrie.create(Set.of("ac"));

    // When
    final var result = sut.put(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, true, false, false);
  }

  @Test
  void shouldDelete() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of(key));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false);
  }

  @Test
  void shouldDeleteCaseZeroChildSuffix() {
    // Given
    final var key = "abc";
    final var sut = RadixTrie.create(Set.of("a", "abc"));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false);
  }

  @Test
  void shouldDeleteCaseEmptyKey() {
    // Given
    final var key = "";
    final var sut = RadixTrie.create(Set.of("a"));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    // not possible to delete root node
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false);
  }

  @Test
  void shouldDeleteCaseNonExistentKey() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create();

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).isEmpty();
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false);
  }

  @Test
  void shouldDeleteCaseDanglingNode() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of(key));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false);
  }

  @Test
  void shouldDeleteCaseNonDanglingNode() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of(key, "ab", "ac"));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, true, false, false);
  }

  @Test
  void shouldDeleteCasePassThrough() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of(key, "ab"));

    // When
    final var result = sut.delete(key);
    final var nodes = nodes(sut);

    // Then
    Assertions.assertThat(result).get().isEqualTo(key);
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "ab");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false);
  }

  @Test
  void shouldCheckContains() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create(Set.of(key));

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckContainsCaseNonExistentKey() {
    // Given
    final var key = "a";
    final var sut = RadixTrie.create();

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldCheckContainsCaseZeroChildSuffix() {
    // Given
    final var key = "ab";
    final var sut = RadixTrie.create(Set.of("a", "ab"));

    // When
    final var result = sut.contains(key);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldGetLongestPrefixCaseFullPrefix() {
    // Given
    final var key = "abc";
    final var sut = RadixTrie.create(Set.of("abc", "abd"));

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
    final var sut = RadixTrie.create(Set.of(keyOne));

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
  void shouldCheckEmptyCaseEmpty() {
    // Given
    final var sut = RadixTrie.create();

    // When
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void shouldCheckEmptyCaseNonEmpty() {
    // Given
    final var sut = RadixTrie.create(Set.of("test"));

    // When
    final var result = sut.empty();

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test
  void shouldAccept() {
    // Given
    final var nodes = new HashSet<RadixTrieNode>();
    final Consumer<RadixTrieNode> trieProcessor = root -> accept(root, nodes);
    final var sut = RadixTrie.create(Set.of("a", "ab", "abc"));

    // When
    sut.accept(trieProcessor);

    // Then
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::getPrefix)
        .containsOnly("", "a", "b", "c");
    Assertions.assertThat(nodes)
        .extracting(RadixTrieNode::isIntermediate)
        .containsOnly(false, false, false, false);
  }

  private Set<RadixTrieNode> nodes(RadixTrie trie) {
    var nodes = new HashSet<RadixTrieNode>();
    final Consumer<RadixTrieNode> trieProcessor = root -> accept(root, nodes);
    trie.accept(trieProcessor);
    return nodes;
  }

  private void accept(RadixTrieNode node, Set<RadixTrieNode> nodes) {
    nodes.add(node);
    node.getCharToChild().values().forEach(childNode -> accept(childNode, nodes));
  }
}