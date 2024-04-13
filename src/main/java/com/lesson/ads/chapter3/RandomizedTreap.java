package com.lesson.ads.chapter3;

import com.lesson.ads.chapter3.Treap.Entry;
import java.util.Optional;
import java.util.Random;

public class RandomizedTreap<K extends Comparable<K>> {

  private final Random random = new Random();
  private final Treap<K, Double> treap = new Treap<>();

  public Optional<K> get(K key) {
    return treap.getElement(key).map(Entry::key);
  }

  public Optional<K> min() {
    return treap.getMinKeyElement().map(Entry::key);
  }

  public Optional<K> max() {
    return treap.getMaxKeyElement().map(Entry::key);
  }

  public K put(K key) {
    treap.put(key, random.nextDouble());
    return key;
  }

  public K delete(K key) {
    return treap.delete(key).key();
  }
}
