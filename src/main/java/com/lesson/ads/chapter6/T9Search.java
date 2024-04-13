package com.lesson.ads.chapter6;

import com.lesson.ads.chapter6.Trie.TrieNode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class T9Search {

  private final Trie trie;
  private final Map<Integer, Set<Character>> buttonToCharacters;

  public T9Search(Collection<String> dictionary) {
    validateDictionary(dictionary);
    this.trie = Trie.create(dictionary);
    this.buttonToCharacters = buttonToCharacters();
  }

  private static void validateDictionary(Collection<String> dictionary) {
    if (dictionary == null) {
      throw new IllegalArgumentException("Dictionary cannot be null");
    }
  }

  private static Map<Integer, Set<Character>> buttonToCharacters() {
    return Map.of(
        1, Set.of(),
        2, Set.of('a', 'b', 'c'),
        3, Set.of('d', 'e', 'f'),
        4, Set.of('g', 'h', 'i'),
        5, Set.of('j', 'k', 'l'),
        6, Set.of('m', 'n', 'o'),
        7, Set.of('p', 'q', 'r', 's'),
        8, Set.of('t', 'u', 'v'),
        9, Set.of('w', 'x', 'y', 'z')
    );
  }

  public T9SearchResult search(int... pressedButtons) {
    validatePressedButtons(pressedButtons);
    var result = new HashSet<String>();
    trie.accept(root -> accept(root, "", pressedButtons, result));
    return result.stream()
        .sorted(Comparator.comparing(String::length))
        .collect(Collectors.collectingAndThen(Collectors.toList(), T9SearchResult::new));
  }

  private void validatePressedButtons(int... pressedButtons) {
    if (pressedButtons == null) {
      throw new IllegalArgumentException("Pressed buttons cannot be null");
    }
    Arrays.stream(pressedButtons)
        .filter(button -> button < 1 || button > 9)
        .findFirst()
        .ifPresent(button -> {
          throw new IllegalArgumentException("Pressed buttons cannot be out of range 1..9");
        });
  }

  private void accept(TrieNode node, String prefix, int[] pressedButtons, Set<String> result) {
    if (prefix.length() == pressedButtons.length) {
      var keys = trie.keys(prefix);
      result.addAll(keys);
    } else {
      var pressedButton = pressedButtons[prefix.length()];
      var characters = buttonToCharacters.get(pressedButton);
      node.getCharToChild().forEach((character, childNode) -> {
        if (characters.contains(character)) {
          accept(childNode, prefix + character, pressedButtons, result);
        }
      });
    }
  }

  public record T9SearchResult(List<String> recommendations) {

  }
}