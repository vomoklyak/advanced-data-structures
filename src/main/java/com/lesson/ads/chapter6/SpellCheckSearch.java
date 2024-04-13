package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.Trie.TrieNode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpellCheckSearch {

  private final Trie trie;

  public SpellCheckSearch(Collection<String> dictionary) {
    validateDictionary(dictionary);
    this.trie = Trie.create(dictionary);
  }

  private static void validateDictionary(Collection<String> dictionary) {
    if (dictionary == null) {
      throw new IllegalArgumentException("Dictionary cannot be null");
    }
  }

  public SpellCheckSearchResult search(String word) {
    validateWord(word);
    return new SpellCheckSearchResult(word, trie.contains(word), List.of());
  }

  public SpellCheckSearchResult search(String word, int maxLevenshteinDistance) {
    validateWord(word);
    validateMaxLevenshteinDistance(maxLevenshteinDistance);
    var wordLength = word.length();
    var maxWordLength = word.length() + maxLevenshteinDistance;
    var levenshteinMatrix =
        levenshteinMatrix(maxWordLength + 1, wordLength + 1);
    var result = new HashMap<String, Integer>();
    Consumer<TrieNode> trieProcessor = root ->
        root.getCharToChild().forEach((character, childNode) -> accept(childNode, "" + character,
            word, maxLevenshteinDistance, levenshteinMatrix.clone(), result));
    trie.accept(trieProcessor);
    return toSpellCheckSearchResult(word, result);
  }

  private void validateWord(String word) {
    if (word == null) {
      throw new IllegalArgumentException("Word cannot be null");
    }
  }

  private void validateMaxLevenshteinDistance(int maxLevenshteinDistance) {
    if (maxLevenshteinDistance < 0) {
      throw new IllegalArgumentException("Max Levenshtein distance cannot be negative");
    }
  }

  private int[][] levenshteinMatrix(int numberOfRows, int numberOfColumns) {
    var levenshteinMatrix = new int[numberOfRows][numberOfColumns];
    IntStream.range(0, numberOfRows)
        .forEach(row -> levenshteinMatrix[row][0] = row);
    IntStream.range(0, numberOfColumns)
        .forEach(column -> levenshteinMatrix[0][column] = column);
    return levenshteinMatrix;
  }

  private void accept(
      TrieNode node,
      String prefix,
      String word,
      int maxLevenshteinDistance,
      int[][] levenshteinMatrix,
      Map<String, Integer> result) {
    if (prefix.length() <= word.length() + maxLevenshteinDistance) {
      IntStream.rangeClosed(1, word.length()).forEach(column -> {
        var charDeletion = levenshteinMatrix[prefix.length()][column - 1] + 1;
        var charInsertion = levenshteinMatrix[prefix.length() - 1][column] + 1;
        var charSubstitution = levenshteinMatrix[prefix.length() - 1][column - 1] +
            (prefix.charAt(prefix.length() - 1) == word.charAt(column - 1) ? 0 : 1);
        levenshteinMatrix[prefix.length()][column] =
            Math.min(Math.min(charDeletion, charInsertion), charSubstitution);
      });
      var wordNode = !node.isIntermediate();
      var levenshteinDistance = levenshteinMatrix[prefix.length()][word.length()];
      if (wordNode && levenshteinDistance <= maxLevenshteinDistance) {
        result.put(prefix, levenshteinDistance);
      }
      node.getCharToChild().forEach((character, childNode) -> accept(childNode,
          prefix + character, word, maxLevenshteinDistance, levenshteinMatrix.clone(), result));
    }
  }

  private SpellCheckSearchResult toSpellCheckSearchResult(
      String word, Map<String, Integer> wordToLevenshteinDistance) {
    var valid = wordToLevenshteinDistance.remove(word) != null;
    return wordToLevenshteinDistance.entrySet().stream()
        .sorted(Entry.comparingByValue())
        .map(Entry::getKey)
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            recommendations -> new SpellCheckSearchResult(word, valid, recommendations)));
  }

  public record SpellCheckSearchResult(String word, boolean valid, List<String> recommendations) {

  }
}