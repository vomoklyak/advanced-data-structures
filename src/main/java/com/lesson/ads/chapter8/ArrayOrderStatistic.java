package com.lesson.ads.chapter8;

import java.util.Comparator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayOrderStatistic {

  // 2*n
  public static <T> void selectKth(
      T[] array, int startIndex, int endIndex, Comparator<T> comparator, int kth) {
    int leftIndex = startIndex;
    int rightIndex = endIndex;

    while (leftIndex < rightIndex) {
      int pivotIndex = pivotIndex(array, leftIndex, rightIndex, comparator);
      if (kth < pivotIndex) {
        rightIndex = pivotIndex - 1;
      } else if (kth > pivotIndex) {
        leftIndex = pivotIndex + 1;
      } else {
        return;
      }
    }
  }

  private static <T> int pivotIndex(
      T[] array, int startIndex, int endIndex, Comparator<T> comparator) {
    var pivotValue = array[startIndex];
    var leftIndex = startIndex;
    var rightIndex = endIndex + 1;
    while (true) {
      while (comparator.compare(array[++leftIndex], pivotValue) < 0) {
        if (leftIndex == endIndex) {
          break;
        }
      }
      while (comparator.compare(array[--rightIndex], pivotValue) > 0) {
        if (rightIndex == startIndex) {
          break;
        }
      }
      if (leftIndex >= rightIndex) {
        break;
      }
      swap(array, leftIndex, rightIndex);
    }
    swap(array, startIndex, rightIndex);
    return rightIndex;
  }

  private static void swap(Object[] array, int leftIndex, int rightIndex) {
    var leftValue = array[leftIndex];
    array[leftIndex] = array[rightIndex];
    array[rightIndex] = leftValue;
  }
}