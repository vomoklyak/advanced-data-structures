package com.lesson.ads.chapter12;

import com.lesson.ads.chapter8.KdTree;
import com.lesson.ads.util.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CanopyClusterSearch {

  private final int dimension;

  public CanopyClusterSearch(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public CanopyClusterSearchResult search(double minRadius, double maxRadius, Point... points) {
    validateRadius(minRadius, maxRadius);
    validatePoints(dimension, points);
    var clusters = new ArrayList<CanopyCluster>();
    var pointKdTree = new KdTree(dimension, points);
    var pointToIndex = IntStream.range(0, points.length)
        .boxed()
        .collect(Collectors.toMap(pointIndex -> points[pointIndex], Function.identity()));

    while (!pointToIndex.isEmpty()) {
      var center = pointToIndex.keySet().iterator().next();
      var clusterPoints = pointKdTree.getRegionPoints(center, maxRadius);
      var clusterPointIndexes = clusterPoints.stream()
          .filter(pointToIndex::containsKey)
          .mapToInt(point -> center.distance(point) <= minRadius ?
              pointToIndex.remove(point) : pointToIndex.get(point))
          .sorted()
          .toArray();
      clusters.add(new CanopyCluster(center, clusterPointIndexes));
    }

    return new CanopyClusterSearchResult(points, clusters.toArray(new CanopyCluster[0]));
  }

  private static void validateRadius(double minRadius, double maxRadius) {
    if (minRadius <= 0) {
      throw new IllegalArgumentException(String.format(
          "Min radius should be positive: minRadius=%s", minRadius));
    }
    if (maxRadius <= 0) {
      throw new IllegalArgumentException(String.format(
          "Max radius should be positive: maxRadius=%s", maxRadius));
    }
    if (maxRadius < minRadius) {
      throw new IllegalArgumentException(String.format(
          "Max radius should be greater or equal to min radius: minRadius=%s, maxRadius=%s",
          minRadius, maxRadius));
    }
  }

  private static void validatePoints(int dimension, Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException("Points cannot be null");
    }
    Arrays.stream(points)
        .filter(point -> point.dimension() != dimension)
        .findFirst()
        .ifPresent(point -> {
          throw new IllegalArgumentException(String.format(
              "Point has incorrect dimension: expectedDimension=%s, point=%s", dimension, point));
        });
  }

  public static record CanopyClusterSearchResult(
      Point[] points,
      CanopyCluster[] clusters) {

    public Point[] centroids() {
      return Arrays.stream(clusters)
          .map(cluster -> {
            var dimension = cluster.center.dimension();
            double[] centroidCoordinates = IntStream.range(0, dimension).boxed()
                .map(coordinate -> mean(coordinate, cluster.clusterPointIndexes()))
                .mapToDouble(any -> any)
                .toArray();
            return new Point(centroidCoordinates);
          })
          .toArray(Point[]::new);
    }

    private double mean(int coordinate, int[] pointIndexes) {
      return (1D / pointIndexes.length) * Arrays.stream(pointIndexes)
          .mapToDouble(pointIndex -> points[pointIndex].coordinate(coordinate)).sum();
    }
  }

  public static record CanopyCluster(
      Point center,
      int[] clusterPointIndexes) {

  }
}
