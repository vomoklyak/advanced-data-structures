package com.lesson.ads.chapter4;

import com.google.common.hash.Hashing;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BloomFilter {

  private final BitSet bitSet;
  private final List<BitHashFunction> bitHashFunctions;

  public static BloomFilter create(int numberOfBits, int numberOfBitHashFunctions, int seed) {
    validateNumberOfBits(numberOfBits);
    validateNumberOfBitHashFunctions(numberOfBitHashFunctions);
    return new BloomFilter(new BitSet(numberOfBits),
        bitHashFunctions(numberOfBits, numberOfBitHashFunctions, seed));
  }

  private static void validateNumberOfBits(int numberOfBits) {
    if (numberOfBits < 1) {
      throw new IllegalArgumentException(String.format(
          "Number of bits should be positive: numberOfBits=%s", numberOfBits));
    }
  }

  private static void validateNumberOfBitHashFunctions(int numberOfBitHashFunctions) {
    if (numberOfBitHashFunctions < 1) {
      throw new IllegalArgumentException(String.format(
          "Number of bit hash functions should be positive: numberOfBits=%s",
          numberOfBitHashFunctions));
    }
  }

  public static BloomFilter approximate(int seed, double precision, int expectedNumberOfKeys) {
    validatePrecision(precision);
    validateExpectedNumberOfKeys(expectedNumberOfKeys);
    var bloomFilterApproximator = new BloomFilterApproximator();
    var numberOfBits = bloomFilterApproximator
        .approximateNumberOfBits(precision, expectedNumberOfKeys);
    var numberOfBitHashFunctions = bloomFilterApproximator
        .approximateNumberOfBitHashFunctions(precision);
    return new BloomFilter(new BitSet(numberOfBits),
        bitHashFunctions(numberOfBits, numberOfBitHashFunctions, seed));
  }

  private static void validateExpectedNumberOfKeys(int expectedNumberOfKeys) {
    if (expectedNumberOfKeys < 1) {
      throw new IllegalArgumentException(String.format(
          "Expected number of keys should be positive: expectedNumberOfKeys=%s",
          expectedNumberOfKeys));
    }
  }

  private static void validatePrecision(double precision) {
    if (precision <= 0 || precision > 1) {
      throw new IllegalArgumentException(String.format(
          "Precision should belong to interval: (0, 1), precision=%s", precision));
    }
  }

  private static List<BitHashFunction> bitHashFunctions(
      int numberOfBits, int numberOfBitHashFunctions, int seed) {
    return IntStream.range(0, numberOfBitHashFunctions)
        .mapToObj(index -> bitHashFunction(numberOfBits, seed, index))
        .collect(Collectors.toList());
  }

  private static BitHashFunction bitHashFunction(int numberOfBits, int seed, int index) {
    var adler = Hashing.adler32();
    var murmur3 = Hashing.murmur3_32_fixed(seed);
    return bytes -> {
      // murmur3Hash + index*adlerHash + index*index
      var hash = (murmur3.hashBytes(bytes).asInt() +
          index * adler.hashBytes(bytes).asInt() + index * index);
      return Math.abs(hash) % numberOfBits;
    };
  }

  public void add(byte[] key) {
    validateKey(key);
    bitHashFunctions.forEach(
        bitHashFunction -> bitSet.set(bitHashFunction.hash(key)));
  }

  public boolean contains(byte[] key) {
    validateKey(key);
    return bitHashFunctions.stream()
        .allMatch(bitHashFunction -> bitSet.get(bitHashFunction.hash(key)));
  }

  private static void validateKey(byte[] key) {
    Objects.requireNonNull(key);
  }

  public int numberOfBits() {
    return bitSet.size();
  }

  public int numberOfBitHashFunctions() {
    return bitHashFunctions.size();
  }

  @FunctionalInterface
  public interface BitHashFunction {

    int hash(byte[] bytes);
  }

  public static class BloomFilterApproximator {

    public int approximateNumberOfBits(double precision, int numberOfKeys) {
      return (int) (-numberOfKeys * Math.log(1 - precision) / (Math.log(2) * Math.log(2)));
    }

    public int approximateNumberOfBitHashFunctions(double precision) {
      return (int) (-Math.log(1 - precision) / Math.log(2));
    }
  }
}