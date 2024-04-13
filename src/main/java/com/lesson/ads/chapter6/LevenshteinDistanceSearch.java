package com.lesson.ads.chapter6;

import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LevenshteinDistanceSearch {

  public static int recursiveSearch(String left, String right) {
    validate(left, right);
    return recursiveSearch(left, right, 0, 0);
  }

  private static int recursiveSearch(
      CharSequence left, CharSequence right, int leftIndex, int rightIndex) {
    if (leftIndex >= left.length()) {
      return right.length() - rightIndex;
    }
    if (rightIndex >= right.length()) {
      return left.length() - leftIndex;
    }
    var charDeletion =
        recursiveSearch(left, right, leftIndex + 1, rightIndex) + 1;
    var charInsertion =
        recursiveSearch(left, right, leftIndex, rightIndex + 1) + 1;
    var charSubstitution =
        recursiveSearch(left, right, leftIndex + 1, rightIndex + 1) +
            (left.charAt(leftIndex) == right.charAt(rightIndex) ? 0 : 1);
    return Math.min(Math.min(charDeletion, charInsertion), charSubstitution);
  }

  public static int dpSearch(String left, String right) {
    validate(left, right);
    var matrix = new int[left.length() + 1][right.length() + 1];
    IntStream.rangeClosed(0, left.length())
        .forEach(row -> matrix[row][0] = row);
    IntStream.rangeClosed(0, right.length())
        .forEach(column -> matrix[0][column] = column);
    IntStream.rangeClosed(1, left.length()).forEach(row ->
        IntStream.rangeClosed(1, right.length()).forEach(column -> {
          var charDeletion = matrix[row][column - 1] + 1;
          var charInsertion = matrix[row - 1][column] + 1;
          var charSubstitution = matrix[row - 1][column - 1] +
              (left.charAt(row - 1) == right.charAt(column - 1) ? 0 : 1);
          matrix[row][column] = Math.min(
              Math.min(charDeletion, charInsertion), charSubstitution);
        }));
    return matrix[left.length()][right.length()];
  }

  private static void validate(CharSequence left, CharSequence right) {
    if (left == null || right == null) {
      throw new IllegalArgumentException(String.format(
          "Input strings cannot be null: left=%s, right=%s", left, right));
    }
  }
}