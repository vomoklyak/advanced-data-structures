package com.lesson.ads.chapter5;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DisjointSets<E> {

  private final Map<E, TreeInfo> elementToTreeInfo;

  public DisjointSets() {
    this.elementToTreeInfo = new HashMap<>();
  }

  public DisjointSets(Collection<E> elements) {
    this.elementToTreeInfo = elements.stream()
        .collect(Collectors.toMap(element -> element, TreeInfo::new));
  }

  public E add(E element) {
    validateElementNotPresent(element);
    elementToTreeInfo.put(element, new TreeInfo(element));
    return element;
  }

  private void validateElementNotPresent(E element) {
    if (elementToTreeInfo.containsKey(element)) {
      throw new IllegalArgumentException(
          String.format("Element already present: element=%s", element));
    }
  }

  public E findRoot(E element) {
    validateElementPresent(element);
    return findRootTreeInfo(element).root;
  }

  public boolean merge(E left, E right) {
    validateElementPresent(left);
    validateElementPresent(right);
    var leftTreeInfo = findRootTreeInfo(left);
    var rightTreeInfo = findRootTreeInfo(right);
    if (leftTreeInfo.root == rightTreeInfo.root) {
      return false;
    }
    if (leftTreeInfo.cardinality < rightTreeInfo.cardinality) {
      rightTreeInfo.merge(leftTreeInfo);
    } else {
      leftTreeInfo.merge(rightTreeInfo);
    }
    return true;
  }

  private TreeInfo findRootTreeInfo(E element) {
    var treeInfo = elementToTreeInfo.get(element);
    if (treeInfo.root == element) {
      return treeInfo;
    }
    // path compression
    treeInfo.root = findRootTreeInfo(treeInfo.root).root;
    return treeInfo;
  }

  private void validateElementPresent(E element) {
    if (!elementToTreeInfo.containsKey(element)) {
      throw new IllegalArgumentException(
          String.format("Element not present: element=%s", element));
    }
  }

  public Collection<Set<E>> disjointSets() {
    var disjointSets = new HashMap<E, Set<E>>();
    // aggregate by root
    this.elementToTreeInfo.keySet().forEach(element -> {
      var treeInfo = findRootTreeInfo(element);
      disjointSets.computeIfAbsent(treeInfo.root, key -> new HashSet<>()).add(element);
    });
    return disjointSets.values();
  }

  private class TreeInfo {

    private E root;
    private int cardinality;

    TreeInfo(E root) {
      this.root = root;
      this.cardinality = 1;
    }

    void merge(TreeInfo that) {
      that.root = this.root;
      this.cardinality += that.cardinality;
    }

    @Override
    public String toString() {
      return String.format("TreeInfo(root=%s,cardinality=%s)", root, cardinality);
    }
  }
}
