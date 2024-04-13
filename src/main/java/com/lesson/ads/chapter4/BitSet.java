package com.lesson.ads.chapter4;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BitSet {

  private final int size;
  private final long[] words;

  public BitSet(int size) {
    validateSize(size);
    this.size = size;
    var numberOfWords = wordIndex(size - 1) + 1;
    words = new long[numberOfWords];
  }

  public void set(int bitIndex) {
    validateBitIndex(bitIndex);
    var wordIndex = wordIndex(bitIndex);
    words[wordIndex] |= (1L << bitIndex);
  }

  public void unset(int bitIndex) {
    validateBitIndex(bitIndex);
    var wordIndex = wordIndex(bitIndex);
    words[wordIndex] &= ~(1L << bitIndex);
  }

  public boolean get(int bitIndex) {
    validateBitIndex(bitIndex);
    var wordIndex = wordIndex(bitIndex);
    return (words[wordIndex] & (1L << bitIndex)) != 0;
  }

  public int size() {
    return size;
  }

  public long[] words() {
    return Arrays.copyOf(words, words.length);
  }

  public String bitStr() {
    return Arrays.stream(words)
        .mapToObj(this::wordStr)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  private String wordStr(long word) {
    var zero = (char) 48;
    var stringBuilder = new StringBuilder();
    var wordBits = Long.toBinaryString(word).chars().toArray();
    for (int bitIndex = 0; bitIndex < 64; bitIndex++) {
      var reversedBit = bitIndex < wordBits.length ?
          wordBits[wordBits.length - bitIndex - 1] : zero;
      stringBuilder.append((char) reversedBit);
    }
    return stringBuilder.toString();
  }

  private void validateSize(int size) {
    if (size < 1) {
      throw new IllegalArgumentException(
          String.format("Size should be positive: size=%s", size));
    }
  }

  private void validateBitIndex(int bitIndex) {
    if (bitIndex < 0 || bitIndex >= size) {
      throw new IllegalArgumentException(
          String.format("Bit index should be in range: bitIndex=%s, [0, %s]", bitIndex, size - 1));
    }
  }

  private int wordIndex(int bitIndex) {
    // divide by 64
    return bitIndex >> 6;
  }
}