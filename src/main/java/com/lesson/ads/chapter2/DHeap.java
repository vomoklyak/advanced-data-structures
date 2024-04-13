package com.lesson.ads.chapter2;

import java.util.Comparator;
import java.util.NoSuchElementException;

@SuppressWarnings({"unchecked"})
public class DHeap<P extends Comparable<P>> {

  private final int branchFactor;
  private final Comparator<P> comparator;
  private final Object[] elements;
  private int lastElementIndex;

  public DHeap(int size, int branchFactor) {
    this(size, branchFactor, Comparator.naturalOrder());
  }

  public DHeap(int size, int branchFactor, Comparator<P> comparator) {
    if (size < 0) {
      throw new IllegalArgumentException("Parameter size should be not negative");
    }
    if (branchFactor < 2) {
      throw new IllegalArgumentException("Parameter branchingFactor should be greater than 1");
    }
    if (comparator == null) {
      throw new IllegalArgumentException("Parameter comparator should not null");
    }

    this.branchFactor = branchFactor;
    this.comparator = comparator;
    this.elements = new Object[size];
    this.lastElementIndex = -1;
  }

  public void add(P element) {
    if (lastElementIndex == elements.length - 1) {
      throw new NoSuchElementException("Queue is full");
    }
    elements[++lastElementIndex] = element;
    popUp(lastElementIndex);
  }

  private void popUp(int index) {
    int childIndex = index;
    int parentIndex = parentIndex(index);
    while (less(childIndex, parentIndex)) {
      swop(parentIndex, childIndex);
      childIndex = parentIndex;
      parentIndex = parentIndex(parentIndex);
    }
  }

  public P pop() {
    if (lastElementIndex == -1) {
      throw new NoSuchElementException("Queue is empty");
    }
    P element = (P) elements[0];
    swop(0, lastElementIndex);
    elements[lastElementIndex--] = null;
    pushDown(0);
    return element;
  }

  private void pushDown(int index) {
    int parentIndex = index;
    int minChildIndex = index;
    while (true) {
      for (
          int childIndex = parentIndex * branchFactor + 1;
          childIndex <= (parentIndex + 1) * branchFactor;
          childIndex++
      ) {
        if (childIndex > lastElementIndex || elements[childIndex] == null) {
          break;
        }
        if (less(childIndex, minChildIndex)) {
          minChildIndex = childIndex;
        }
      }
      if (parentIndex != minChildIndex) {
        swop(parentIndex, minChildIndex);
        parentIndex = minChildIndex;
      } else {
        break;
      }
    }
  }

  private int parentIndex(int childIndex) {
    return (childIndex - 1) / branchFactor;
  }

  private boolean less(int leftIndex, int rightIndex) {
    return comparator.compare((P) elements[leftIndex], (P) elements[rightIndex]) < 0;
  }

  private void swop(int leftIndex, int rightIndex) {
    var parentElement = elements[leftIndex];
    elements[leftIndex] = elements[rightIndex];
    elements[rightIndex] = parentElement;
  }
}
