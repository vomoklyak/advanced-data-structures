package com.lesson.ads.chapter7;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

public class LruCache<K, V> {

  private final int maxSize;
  private final LruCacheNodeLinkedList nodes;
  private final Map<K, LruCacheNode> keyToNode;

  public LruCache(int maxSize) {
    validateMaxSize(maxSize);
    this.maxSize = maxSize;
    this.nodes = new LruCacheNodeLinkedList();
    this.keyToNode = new HashMap<>();
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
      nodes.delete(node);
    } else if (keyToNode.size() >= maxSize) {
      K deletedNodeKey = nodes.deleteLast().key();
      keyToNode.remove(deletedNodeKey);
    }
    node = nodes.addFirst(new LruCacheNode(key, value));
    keyToNode.put(key, node);
    return value;
  }

  public Optional<V> get(K key) {
    validateKey(key);
    var nodeOpt = nodeOpt(key);
    nodeOpt.ifPresent(node -> {
      nodes.delete(node);
      nodes.addFirst(node);
    });
    return nodeOpt
        .map(LruCacheNode::value);
  }

  public Optional<V> delete(K key) {
    validateKey(key);
    var nodeOpt = nodeOpt(key);
    nodeOpt.ifPresent(node -> {
      nodes.delete(node);
      keyToNode.remove(key);
    });
    return nodeOpt
        .map(LruCacheNode::value);
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

  private Optional<LruCacheNode> nodeOpt(K key) {
    return Optional.ofNullable(keyToNode.get(key));
  }

  private static class LruCacheNodeLinkedList {

    private LruCacheNode head;
    private LruCacheNode tail;

    LruCacheNode addFirst(LruCacheNode node) {
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

    LruCacheNode deleteLast() {
      return delete(tail);
    }

    LruCacheNode delete(LruCacheNode node) {
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

  @RequiredArgsConstructor
  @SuppressWarnings("unchecked")
  private static class LruCacheNode {

    private final Object key;
    private final Object value;

    private LruCacheNode prev;
    private LruCacheNode next;

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
}
