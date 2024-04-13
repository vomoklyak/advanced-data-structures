package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.ClusterViewer.ClusterView;
import com.lesson.ads.chapter8.KdTree;
import com.lesson.ads.util.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DbscanClusterSearch {

  public static void main(String[] args) {
    var searchResult = new DbscanClusterSearch(2)
        .search(2, 1D, Datasets.spiral());
    ClusterViewer.view(searchResult);
  }

  private static final int OUTLIER = -1;

  private final int dimension;

  public DbscanClusterSearch(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public DbscanClusterSearchResult search(
      int coreNumberOfNeighbors, double coreRadius, Point... points) {
    validateCoreNumberOfNeighbors(coreNumberOfNeighbors);
    validateCoreRadius(coreRadius);
    validatePoints(dimension, points);

    var pointTree = new KdTree(dimension, points);
    var clusterIndexer = new AtomicInteger(-1);
    var pointToClusterIndex = new HashMap<Point, Integer>();

    Arrays.stream(points).forEach(point -> {
      clusterIndexer.incrementAndGet();
      var neighbors = new HashSet<>(List.of(point));
      while (neighbors.size() != 0) {
        var neighborPoint = neighbors.iterator().next();
        neighbors.remove(neighborPoint);
        if (!pointToClusterIndex.containsKey(neighborPoint)) {
          // not processed yet
          var neighborNeighbors = pointTree.getRegionPoints(neighborPoint, coreRadius);
          if (neighborNeighbors.size() >= coreNumberOfNeighbors) {
            // directly reachable point
            pointToClusterIndex.put(neighborPoint, clusterIndexer.get());
            neighborNeighbors.stream()
                .filter(neighborNeighbor -> !pointToClusterIndex.containsKey(neighborNeighbor))
                .forEach(neighbors::add);
          } else if (neighborPoint.equals(point)) {
            // outlier point
            clusterIndexer.decrementAndGet();
          } else {
            // reachable by density point
            pointToClusterIndex.put(neighborPoint, clusterIndexer.get());
          }
        } else {
          // already processed point
          clusterIndexer.decrementAndGet();
        }
      }
    });
    var pointClusterIndexes = Arrays.stream(points)
        .mapToInt(key -> pointToClusterIndex.getOrDefault(key, OUTLIER))
        .toArray();
    return new DbscanClusterSearchResult(points, pointClusterIndexes,
        clusterIndexer.incrementAndGet());
  }

  private static void validateCoreNumberOfNeighbors(int coreNumberOfNeighbors) {
    if (coreNumberOfNeighbors < 1) {
      throw new IllegalArgumentException(String.format(
          "Core number of neighbors should be positive: coreNumberOfNeighbors=%s",
          coreNumberOfNeighbors));
    }
  }

  private static void validateCoreRadius(double coreRadius) {
    if (coreRadius <= 0) {
      throw new IllegalArgumentException(String.format(
          "Core radius should be positive: coreRadius=%s", coreRadius));
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

  public static record DbscanClusterSearchResult(
      Point[] points,
      int[] pointClusterIndexes,
      int numberOfClusters) implements ClusterView {

    @Override
    public Point[] points() {
      return points;
    }

    @Override
    public int[] pointClusterIndexes() {
      return pointClusterIndexes;
    }

    @Override
    public Point[] clusterCentroids() {
      return new Point[0];
    }

    @Override
    public int outlierIndex() {
      return OUTLIER;
    }
  }
}