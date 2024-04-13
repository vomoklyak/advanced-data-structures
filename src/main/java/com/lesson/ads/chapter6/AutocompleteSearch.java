package com.lesson.ads.chapter6;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutocompleteSearch {

  private final Trie trie;

  public AutocompleteSearch(Collection<String> dictionary) {
    validateDictionary(dictionary);
    this.trie = Trie.create(dictionary);
  }

  private static void validateDictionary(Collection<String> dictionary) {
    if (dictionary == null) {
      throw new IllegalArgumentException("Dictionary cannot be null");
    }
  }

  public AutocompleteSearchResult search(String prefix) {
    validatePrefix(prefix);
    return trie.keys(prefix).stream()
        .sorted(Comparator.comparing(String::length))
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            recommendations -> new AutocompleteSearchResult(prefix, recommendations)));
  }

  private void validatePrefix(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException("Prefix cannot be null");
    }
  }

  public record AutocompleteSearchResult(String prefix, List<String> recommendations) {

  }
}