package com.lesson.ads.chapter6;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RadixTrie {

  private final RadixTrieNode root;

  public static RadixTrie create() {
    return new RadixTrie(new RadixTrieNode("", false));
  }

  public static RadixTrie create(Collection<String> keys) {
    validateKeys(keys);
    var radixTrie = create();
    keys.forEach(radixTrie::put);
    return radixTrie;
  }

  private static void validateKeys(Collection<String> keys) {
    if (keys == null) {
      throw new IllegalArgumentException("Keys cannot be null");
    }
  }

  public String put(String key) {
    validateKey(key);
    return key.isEmpty() ? key : put(root, key);
  }

  private String put(RadixTrieNode node, String key) {
    var childNode = node.child(key);
    if (childNode == null) {
      node.addChild(new RadixTrieNode(key, false));
    } else {
      var commonPrefixLength = commonPrefixLength(key, childNode.prefix);
      var commonPrefix = key.substring(0, commonPrefixLength);
      var keySuffix = key.substring(commonPrefixLength);
      var childSuffix = childNode.prefix.substring(commonPrefixLength);
      if (keySuffix.length() == 0 && childSuffix.length() == 0) {
        childNode.intermediate = false;
      } else if (keySuffix.length() == 0) {
        var commonPrefixNode = new RadixTrieNode(commonPrefix, false);
        childNode.prefix = childSuffix;
        commonPrefixNode.addChild(childNode);
        node.addChild(commonPrefixNode);
      } else if (childSuffix.length() == 0) {
        put(childNode, keySuffix);
      } else {
        var commonPrefixNode = new RadixTrieNode(commonPrefix, true);
        var keySuffixNode = new RadixTrieNode(keySuffix, false);
        childNode.prefix = childSuffix;
        commonPrefixNode.addChild(childNode);
        commonPrefixNode.addChild(keySuffixNode);
        node.addChild(commonPrefixNode);
      }
    }
    return key;
  }

  public Optional<String> delete(String key) {
    validateKey(key);
    return key.isEmpty() ? Optional.of(key) : delete(root, key).map(any -> key);
  }

  public Optional<String> delete(RadixTrieNode node, String key) {
    var childNode = node.child(key);
    if (childNode != null) {
      var commonPrefixLength = commonPrefixLength(key, childNode.prefix);
      var keySuffix = key.substring(commonPrefixLength);
      var childSuffix = childNode.prefix.substring(commonPrefixLength);
      if (keySuffix.length() == 0 && childSuffix.length() == 0) {
        if (!childNode.intermediate) {
          childNode.intermediate = true;
          deleteDanglingNode(node, childNode);
          deletePassThroughNode(childNode);
          return Optional.of(key);
        }
      } else if (childSuffix.length() == 0) {
        return delete(childNode, keySuffix);
      }
    }
    return Optional.empty();
  }

  private void deleteDanglingNode(RadixTrieNode parentNode, RadixTrieNode node) {
    if (node.dangling()) {
      parentNode.removeChild(node);
      deletePassThroughNode(parentNode);
    }
  }

  private void deletePassThroughNode(RadixTrieNode node) {
    if (node.passThrough()) {
      var childNode = node.charToChild.values().iterator().next();
      node.prefix = node.prefix + childNode.prefix;
      node.intermediate = childNode.intermediate;
      node.charToChild.clear();
      node.charToChild.putAll(childNode.charToChild);
    }
  }

  public boolean contains(String key) {
    validateKey(key);
    return key.isEmpty() || contains(root, key);
  }

  private boolean contains(RadixTrieNode node, String key) {
    var childNode = node.child(key);
    if (childNode != null) {
      var commonPrefixLength = commonPrefixLength(key, childNode.prefix);
      var keySuffix = key.substring(commonPrefixLength);
      var childSuffix = childNode.prefix.substring(commonPrefixLength);
      if (keySuffix.length() == 0 && childSuffix.length() == 0) {
        return !childNode.intermediate;
      } else if (childSuffix.length() == 0) {
        return contains(childNode, keySuffix);
      }
    }
    return false;
  }

  public String longestPrefix(String key) {
    validateKey(key);
    return key.isEmpty() ? root.prefix : longestPrefix(root, key, new StringBuilder());
  }

  private String longestPrefix(RadixTrieNode node, String key, StringBuilder longestPrefix) {
    var childNode = node.child(key);
    if (childNode != null) {
      var commonPrefixLength = commonPrefixLength(key, childNode.prefix);
      var keySuffix = key.substring(commonPrefixLength);
      var childSuffix = childNode.prefix.substring(commonPrefixLength);
      longestPrefix.append(key, 0, commonPrefixLength);
      if (keySuffix.length() != 0 && childSuffix.length() == 0) {
        longestPrefix(childNode, keySuffix, longestPrefix);
      }
    }
    return longestPrefix.toString();
  }

  private int commonPrefixLength(String left, String right) {
    var commonPrefixLength = 0;
    for (var charIndex = 0; charIndex < Math.min(left.length(), right.length()); charIndex++) {
      if (left.charAt(charIndex) == right.charAt(charIndex)) {
        commonPrefixLength++;
      } else {
        break;
      }
    }
    return commonPrefixLength;
  }

  private static void validateKey(String key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
  }

  protected void accept(Consumer<RadixTrieNode> trieProcessor) {
    validateTrieProcessor(trieProcessor);
    trieProcessor.accept(root);
  }

  private static void validateTrieProcessor(Consumer<RadixTrieNode> trieProcessor) {
    if (trieProcessor == null) {
      throw new IllegalArgumentException("Trie processor cannot be null");
    }
  }

  public boolean empty() {
    return root.charToChild.isEmpty();
  }

  @Getter(AccessLevel.PROTECTED)
  protected static class RadixTrieNode {

    private String prefix;
    private boolean intermediate;
    private final Map<Character, RadixTrieNode> charToChild;

    protected RadixTrieNode(String prefix, boolean intermediate) {
      this.prefix = prefix;
      this.intermediate = intermediate;
      this.charToChild = new HashMap<>();
    }

    protected RadixTrieNode child(String key) {
      return charToChild.get(key.charAt(0));
    }

    protected void addChild(RadixTrieNode node) {
      charToChild.put(node.prefix.charAt(0), node);
    }

    protected void removeChild(RadixTrieNode node) {
      charToChild.remove(node.prefix.charAt(0));
    }

    protected boolean dangling() {
      return intermediate && charToChild.size() == 0;
    }

    protected boolean passThrough() {
      return intermediate && charToChild.size() == 1;
    }

    @Override
    public String toString() {
      return String.format("Node(%s:%s)", prefix, intermediate);
    }
  }
}