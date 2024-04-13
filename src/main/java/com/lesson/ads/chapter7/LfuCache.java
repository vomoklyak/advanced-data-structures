package com.lesson.ads.chapter7;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

public class LfuCache<K, V> {

  private final int maxSize;
  private final Map<K, LfuCacheNode> keyToNode;
  private LfuCacheNodeFrequency minFrequency;

  public LfuCache(int maxSize) {
    validateMaxSize(maxSize);
    this.maxSize = maxSize;
    this.keyToNode = new HashMap<>();
    this.minFrequency = new LfuCacheNodeFrequency();
  }

  private static void validateMaxSize(int maxSize) {
    if (maxSize < 1) {
      throw new IllegalArgumentException(
          String.format("Parameter maxSize should be positive: %s", maxSize));
    }
  }

  public V put(K key, V value) {
    validateKey(key);
    validateValue(value);
    var node = keyToNode.get(key);
    if (node != null) {
      increaseFrequency(node, value);
    } else {
      if (keyToNode.size() >= maxSize) {
        K deletedKey = minFrequency.nodes.deleteLast().key();
        deleteIfEmpty(minFrequency);
        keyToNode.remove(deletedKey);
      }
      node = new LfuCacheNode(key, value, initialFrequency());
      node.frequency.nodes.addFirst(node);
      keyToNode.put(key, node);
    }
    return value;
  }

  private LfuCacheNodeFrequency initialFrequency() {
    if (minFrequency == null || !minFrequency.hasInitialValue()) {
      var initialFrequency = new LfuCacheNodeFrequency();
      initialFrequency.next = minFrequency;
      if (minFrequency != null) {
        minFrequency.prev = initialFrequency;
      }
      minFrequency = initialFrequency;
    }
    return minFrequency;
  }

  public Optional<V> get(K key) {
    validateKey(key);
    var nodeOpt = nodeOpt(key);
    nodeOpt.ifPresent(node -> increaseFrequency(node, null));
    return nodeOpt
        .map(LfuCacheNode::value);
  }

  public Optional<V> delete(K key) {
    validateKey(key);
    var nodeOpt = nodeOpt(key);
    nodeOpt.ifPresent(node -> {
      node.frequency.nodes.delete(node);
      deleteIfEmpty(node.frequency);
      keyToNode.remove(key);
    });
    return nodeOpt
        .map(LfuCacheNode::value);
  }

  private void increaseFrequency(LfuCacheNode node, V newValue) {
    var frequency = node.frequency;
    frequency.nodes.delete(node);
    node = new LfuCacheNode(
        node.key, newValue == null ? node.value : newValue, increasedFrequency(frequency));
    node.frequency.nodes.addFirst(node);
    deleteIfEmpty(frequency);
    keyToNode.put(node.key(), node);
  }

  private LfuCacheNodeFrequency increasedFrequency(LfuCacheNodeFrequency frequency) {
    var increasedFrequencyValue = frequency.value + 1;
    if (frequency.hasNextFrequencyValue(increasedFrequencyValue)) {
      return frequency.next;
    } else {
      var increasedFrequency = new LfuCacheNodeFrequency(increasedFrequencyValue);
      increasedFrequency.prev = frequency;
      increasedFrequency.next = frequency.next;
      frequency.next = increasedFrequency;
      return increasedFrequency;
    }
  }

  private void deleteIfEmpty(LfuCacheNodeFrequency frequency) {
    if (frequency.empty()) {
      if (minFrequency == frequency) {
        minFrequency = frequency.next;
        if (minFrequency != null) {
          minFrequency.prev = null;
        }
      } else {
        frequency.prev.next = frequency.next;
        frequency.next.prev = frequency.prev;
      }
    }
  }

  public int size() {
    return keyToNode.size();
  }

  public int maxSize() {
    return maxSize;
  }

  private void validateKey(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
  }

  private void validateValue(V value) {
    if (value == null) {
      throw new IllegalArgumentException("Value cannot be null");
    }
  }

  private Optional<LfuCacheNode> nodeOpt(K key) {
    return Optional.ofNullable(keyToNode.get(key));
  }

  private static class LruCacheNodeLinkedList {

    private LfuCacheNode head;
    private LfuCacheNode tail;

    LfuCacheNode addFirst(LfuCacheNode node) {
      if (empty()) {
        head = node;
        tail = node;
      } else {
        node.next = head;
        head.prev = node;
        head = node;
      }
      return node;
    }

    LfuCacheNode deleteLast() {
      return delete(tail);
    }

    LfuCacheNode delete(LfuCacheNode node) {
      if (head == node) {
        head = node.next;
        if (head != null) {
          head.prev = null;
        }
      } else if (tail == node) {
        tail = node.prev;
        if (tail != null) {
          tail.next = null;
        }
      } else {
        node.prev.next = node.next;
        node.next.prev = node.prev;
      }
      return node;
    }

    boolean empty() {
      return head == null;
    }
  }

  @SuppressWarnings("unchecked")
  private static class LfuCacheNode {

    private final Object key;
    private final Object value;
    private final LfuCacheNodeFrequency frequency;

    private LfuCacheNode prev;
    private LfuCacheNode next;

    public LfuCacheNode(Object key, Object value, LfuCacheNodeFrequency frequency) {
      this.key = key;
      this.value = value;
      this.frequency = frequency;
    }

    <K> K key() {
      return (K) key;
    }

    <V> V value() {
      return (V) value;
    }

    @Override
    public String toString() {
      return String.format("Node(%s,%s)", key, value);
    }
  }

  @RequiredArgsConstructor
  private static class LfuCacheNodeFrequency {

    private final int value;
    private final LruCacheNodeLinkedList nodes = new LruCacheNodeLinkedList();

    private LfuCacheNodeFrequency prev;
    private LfuCacheNodeFrequency next;

    public LfuCacheNodeFrequency() {
      this.value = 1;
    }

    boolean empty() {
      return nodes.empty();
    }

    boolean hasInitialValue() {
      return value == 1;
    }

    boolean hasNextFrequencyValue(int value) {
      return next != null && next.value == value;
    }
  }
}
