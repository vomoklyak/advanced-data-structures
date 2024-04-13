package com.lesson.ads.chapter3;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;

public class Treap<K extends Comparable<K>, P extends Comparable<P>> {

  private final Comparator<P> priorityComparator;
  private TreapNode root;

  public Treap() {
    this.priorityComparator = Comparator.naturalOrder();
  }

  public Treap(Comparator<P> priorityComparator) {
    this.priorityComparator = priorityComparator;
  }

  public TreapNode asNodes() {
    return root == null ? null : root.copy();
  }

  public int height() {
    var height = 0;
    var queue = new LinkedList<TreapNode>();
    queue.add(root);
    while (!queue.isEmpty()) {
      var node = queue.pop();
      if (node != null) {
        if (node.hasChildren()) {
          queue.add(node.left);
          queue.add(node.right);
        } else {
          height = Math.max(height, height(node));
        }
      }
    }
    return height;
  }

  private int height(TreapNode node) {
    var height = 0;
    var currentNode = node;
    while (currentNode != null) {
      height++;
      currentNode = currentNode.parent;
    }
    return height;
  }

  public Optional<Entry<K, P>> getElement(K key) {
    requireNonNull(key);

    return nodeOpt(key)
        .map(node -> new Entry<>(node.key, node.priority));
  }

  public Optional<Entry<K, P>> getMinKeyElement() {
    var node = root;
    while (node != null && node.hasLeft()) {
      node = node.left;
    }
    return Optional.ofNullable(node)
        .map(nd -> new Entry<>(nd.key, nd.priority));
  }

  public Optional<Entry<K, P>> getMaxKeyElement() {
    var node = root;
    while (node != null && node.hasRight()) {
      node = node.right;
    }
    return Optional.ofNullable(node)
        .map(nd -> new Entry<>(nd.key, nd.priority));
  }

  public Optional<Entry<K, P>> getTopPriorityElement() {
    return Optional.ofNullable(root)
        .map(nd -> new Entry<>(nd.key, nd.priority));
  }

  public Entry<K, P> put(K key, P priority) {
    requireNonNull(key);
    requireNonNull(priority);

    var node = root;
    var parent = root;
    while (node != null) {
      parent = node;
      if (key.compareTo(node.key) == 0) {
        node.priority = priority;
        popUp(node);
        pushDown(node);
        return new Entry<>(key, priority);
      } else if (key.compareTo(node.key) < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }
    if (parent == null) {
      root = new TreapNode(key, priority);
    } else {
      var child = new TreapNode(key, priority);
      parent.setChild(child);
      popUp(child);
    }
    return new Entry<>(key, priority);
  }

  public Entry<K, P> delete(K key) {
    requireNonNull(key);

    var node = nodeOpt(key).orElseThrow(() ->
        new NoSuchElementException(String.format("Element not found: key=%s", key)));
    while (node.hasChildren()) {
      if (node.leftChildHasHigherPriority()) {
        rotateRight(node.left);
      } else {
        rotateLeft(node.right);
      }
    }
    node.parent.deleteChild(node);
    return new Entry<>(node.key, node.priority);
  }

  private Optional<TreapNode> nodeOpt(K key) {
    var node = root;
    while (node != null) {
      if (key.compareTo(node.key) == 0) {
        break;
      } else if (key.compareTo(node.key) < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }
    return Optional.ofNullable(node);
  }

  private void popUp(TreapNode node) {
    while (node.hasParent() && lessPriority(node, node.parent)) {
      if (node.parent.hasLeft(node)) {
        rotateRight(node);
      } else {
        rotateLeft(node);
      }
    }
  }

  private void pushDown(TreapNode node) {
    while (node.hasChildren()) {
      if (node.leftChildHasHigherPriority()) {
        if (lessPriority(node.left, node)) {
          rotateRight(node.left);
        } else {
          break;
        }
      } else {
        if (lessPriority(node.right, node)) {
          rotateLeft(node.right);
        } else {
          break;
        }
      }
    }
  }

  private void rotateLeft(TreapNode node) {
    if (node.hasParent()) {
      var parent = node.parent;
      if (parent.hasParent()) {
        parent.parent.setChild(node);
      } else {
        setRoot(node);
      }
      parent.right = node.left;
      if (parent.hasRight()) {
        parent.right.parent = parent;
      }
      parent.parent = node;
      node.left = parent;
    }
  }

  private void rotateRight(TreapNode node) {
    if (node.hasParent()) {
      var parent = node.parent;
      if (parent.hasParent()) {
        parent.parent.setChild(node);
      } else {
        setRoot(node);
      }
      parent.left = node.right;
      if (parent.hasLeft()) {
        parent.left.parent = parent;
      }
      parent.parent = node;
      node.right = parent;
    }
  }

  private void setRoot(TreapNode node) {
    root = node;
    node.parent = null;
  }

  private void requireNonNull(Object object) {
    Objects.requireNonNull(object);
  }

  private boolean lessPriority(TreapNode leftNode, TreapNode rightNode) {
    return priorityComparator.compare(leftNode.priority, rightNode.priority) < 0;
  }

  public static record Entry<K, P>(K key, P priority) {

  }

  @Getter
  public class TreapNode {

    private final K key;
    private P priority;

    private TreapNode left;
    private TreapNode right;
    private TreapNode parent;

    public TreapNode(K key, P priority) {
      this.key = key;
      this.priority = priority;
    }

    @Override
    public String toString() {
      return String.format("Node(%s:%s)", key, priority);
    }

    boolean hasLeft() {
      return this.left != null;
    }

    boolean hasRight() {
      return this.right != null;
    }

    boolean hasParent() {
      return parent != null;
    }

    boolean hasChildren() {
      return hasLeft() || hasRight();
    }

    boolean hasLeft(TreapNode node) {
      return this.left == node;
    }

    boolean leftChildHasHigherPriority() {
      return left != null &&
          (right == null || lessPriority(left, right));
    }

    void setChild(TreapNode child) {
      child.parent = this;
      if (child.key.compareTo(this.key) <= 0) {
        left = child;
      } else {
        right = child;
      }
    }

    void deleteChild(TreapNode child) {
      if (left == child) {
        left = null;
      } else {
        right = null;
      }
    }

    TreapNode copy() {
      var node = new TreapNode(key, priority);
      if (hasLeft()) {
        node.left = this.left.copy();
        node.left.parent = node;
      }
      if (hasRight()) {
        node.right = this.right.copy();
        node.right.parent = node;
      }
      return node;
    }
  }
}