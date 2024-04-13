package com.lesson.ads.chapter2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HuffmanCoding {

  @SuppressWarnings("ConstantConditions")
  public static String encode(String text) {
    if (text == null) {
      return null;
    }
    if (text.isEmpty()) {
      return "";
    }
    if (text.length() == 1) {
      return "1";
    }
    // build text char frequency priority min queue
    var queue = text.chars()
        .mapToObj(charInt -> String.valueOf((char) charInt))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .map(entry -> TreeNode.of(entry.getKey(), entry.getValue()))
        .collect(Collectors.collectingAndThen(Collectors.toList(), PriorityQueue::new));
    // build prefix frequency binary tree
    while (queue.size() > 1) {
      queue.add(TreeNode.merge(queue.poll(), queue.poll()));
    }
    // build char to code
    var charToCode = new HashMap<String, String>();
    buildCharToCode(queue.poll(), "", charToCode);
    // encode text chars
    return text.chars()
        .mapToObj(charInt -> charToCode.get(String.valueOf((char) charInt)))
        .collect(Collectors.joining());
  }

  private static void buildCharToCode(
      TreeNode node, String code, HashMap<String, String> charToCode) {
    if (node.leaf()) {
      charToCode.put(node.prefix, code);
    } else {
      buildCharToCode(node.leftChild, code + "0", charToCode);
      buildCharToCode(node.rightChild, code + "1", charToCode);
    }
  }

  private record TreeNode(
      String prefix,
      long frequency,
      TreeNode leftChild,
      TreeNode rightChild
  ) implements Comparable<TreeNode> {

    private static final Comparator<TreeNode> COMPARATOR =
        Comparator.comparing(TreeNode::frequency)
            .thenComparing(TreeNode::prefix);

    static TreeNode of(String prefix, long frequency) {
      return new TreeNode(prefix, frequency, null, null);
    }

    static TreeNode merge(TreeNode leftChild, TreeNode rightChild) {
      return new TreeNode(
          leftChild.prefix + rightChild.prefix,
          leftChild.frequency + rightChild.frequency,
          leftChild,
          rightChild
      );
    }

    boolean leaf() {
      return leftChild == null && rightChild == null;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(TreeNode that) {
      return COMPARATOR.compare(this, that);
    }
  }
}