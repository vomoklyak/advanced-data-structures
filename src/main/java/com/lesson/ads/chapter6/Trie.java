package com.lesson.ads.chapter6;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Trie {

  private final TrieNode root;

  public static Trie create() {
    return new Trie(new TrieNode(false));
  }

  public static Trie create(Collection<String> keys) {
    validateKeys(keys);
    var trie = create();
    keys.forEach(trie::put);
    return trie;
  }

  private static void validateKeys(Collection<String> keys) {
    if (keys == null) {
      throw new IllegalArgumentException("Keys cannot be null");
    }
  }

  public String put(String key) {
    validateKey(key);
    var node = root;
    for (char character : key.toCharArray()) {
      node = node.charToChild.computeIfAbsent(
          character, characterKey -> new TrieNode(true));
    }
    node.intermediate = false;
    return key;
  }

  public Optional<String> delete(String key) {
    validateKey(key);
    if (key.isEmpty()) {
      // not possible to delete root node
      return Optional.empty();
    }
    var node = root;
    var keyNodes = new LinkedList<TrieNode>();
    keyNodes.add(node);
    for (char character : key.toCharArray()) {
      node = node.charToChild.get(character);
      keyNodes.add(node);
      if (node == null) {
        return Optional.empty();
      }
    }
    if (node.intermediate) {
      return Optional.empty();
    }
    node.intermediate = true;
    deleteDangling(key, keyNodes);
    return Optional.of(key);
  }

  private void deleteDangling(String key, List<TrieNode> keyNodes) {
    var charIndex = key.length() - 1;
    for (int nodeIndex = keyNodes.size() - 1; nodeIndex >= 1; nodeIndex--) {
      if (keyNodes.get(nodeIndex).dangling()) {
        keyNodes.get(nodeIndex - 1).charToChild.remove(key.charAt(charIndex--));
      } else {
        break;
      }
    }
  }

  public boolean contains(String key) {
    validateKey(key);
    var node = root;
    for (char character : key.toCharArray()) {
      node = node.charToChild.get(character);
      if (node == null) {
        return false;
      }
    }
    return !node.intermediate;
  }

  public String longestPrefix(String key) {
    validateKey(key);
    var node = root;
    var prefix = new StringBuilder();
    for (char character : key.toCharArray()) {
      node = node.charToChild.get(character);
      if (node == null) {
        break;
      } else {
        prefix.append(character);
      }
    }
    return prefix.toString();
  }

  public Set<String> keys(String prefix) {
    validatePrefix(prefix);
    var prefixNode = root;
    for (char character : prefix.toCharArray()) {
      prefixNode = prefixNode.charToChild.get(character);
      if (prefixNode == null) {
        return Set.of();
      }
    }
    var keys = new HashSet<String>();
    keys(prefixNode, prefix, keys);
    return keys;
  }

  private static void validatePrefix(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException("Prefix cannot be null");
    }
  }

  private static void validateKey(String key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
  }

  private void keys(TrieNode prefixNode, String prefix, Set<String> keys) {
    if (!prefixNode.intermediate) {
      keys.add(prefix);
    }
    prefixNode.charToChild.forEach(
        (character, node) -> keys(node, prefix + character, keys));
  }

  public boolean empty() {
    return root.charToChild.isEmpty();
  }

  protected void accept(Consumer<TrieNode> trieProcessor) {
    validateTrieProcessor(trieProcessor);
    trieProcessor.accept(root);
  }

  private static void validateTrieProcessor(Consumer<TrieNode> trieProcessor) {
    if (trieProcessor == null) {
      throw new IllegalArgumentException("Trie processor cannot be null");
    }
  }

  @Getter(AccessLevel.PROTECTED)
  protected static class TrieNode {

    private boolean intermediate;
    private final Map<Character, TrieNode> charToChild;

    protected TrieNode(boolean intermediate) {
      this.intermediate = intermediate;
      this.charToChild = new HashMap<>();
    }

    protected boolean dangling() {
      return intermediate && charToChild.size() == 0;
    }

    @Override
    public String toString() {
      return String.format("Node(%s)", intermediate);
    }
  }
}