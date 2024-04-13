package com.lesson.ads.chapter12;

import com.lesson.ads.chapter12.ClusterViewer.ClusterView;
import com.lesson.ads.chapter8.KdTree;
import com.lesson.ads.util.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpticsClusterSearch {

  public static void main(String[] args) {
    var searchResult = new OpticsClusterSearch(2)
        .search(2, 1D, Datasets.spiral());
    ClusterViewer.view(searchResult.clusterView(1D));
  }

  private static final int OUTLIER = -1;

  private final int dimension;

  public OpticsClusterSearch(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public OpticsClusterSearchResult search(
      int coreNumberOfNeighbors, double maxCoreRadius, Point... points) {
    validateCoreNumberOfNeighbors(coreNumberOfNeighbors);
    validateCoreRadius(maxCoreRadius);
    validatePoints(dimension, points);

    var processedPoints = new HashSet<Point>();
    var pointKdTree = new KdTree(dimension, points);
    var pointToReachabilityDistance = new LinkedHashMap<Point, Double>();
    var pointToMinCoreRadius =
        initPointToMinCoreRadius(coreNumberOfNeighbors, maxCoreRadius, points, pointKdTree);
    Arrays.stream(points)
        .filter(processedPoints::add)
        .forEach(point -> {
          // initial cluster point (reachability distance not defined)
          pointToReachabilityDistance.put(point, Double.POSITIVE_INFINITY);
          var clusterDistancePriorityQueue = new PriorityQueue<ClusterDistance>();
          clusterDistancePriorityQueue.add(new ClusterDistance(point, 0D));
          while (!clusterDistancePriorityQueue.isEmpty()) {
            // nearest to cluster point
            var nearestPoint = clusterDistancePriorityQueue.poll().point();
            var nearestPointNeighbors =
                pointKdTree.getRegionPoints(nearestPoint, maxCoreRadius);
            processedPoints.add(nearestPoint);
            updateNeighborReachabilityDistances(
                nearestPoint,
                nearestPointNeighbors,
                processedPoints,
                pointToMinCoreRadius,
                pointToReachabilityDistance,
                clusterDistancePriorityQueue);
          }
        });
    return new OpticsClusterSearchResult(maxCoreRadius, points, pointToReachabilityDistance);
  }

  private Map<Point, Double> initPointToMinCoreRadius(
      int coreNumberOfNeighbors, double maxCoreRadius, Point[] points, KdTree pointTree) {
    Function<Point, Double> toMinCoreRadius = point -> {
      var neighbors = pointTree.getRegionPoints(point, maxCoreRadius);
      return neighbors.size() >= coreNumberOfNeighbors ?
          // smallest radius that includes core number of neighbors if point is core
          neighbors.stream().map(neighbor ->
              neighbor.distance(point)).sorted().toList().get(coreNumberOfNeighbors - 1) :
          // undefined if point is not core
          Double.POSITIVE_INFINITY;
    };
    return Arrays.stream(points)
        .collect(Collectors.toMap(Function.identity(), toMinCoreRadius));
  }

  private void updateNeighborReachabilityDistances(
      Point point,
      Set<Point> neighbors,
      Set<Point> processedPoints,
      Map<Point, Double> pointToMinCoreRadius,
      Map<Point, Double> pointToReachabilityDistance,
      PriorityQueue<ClusterDistance> nearestNeighborPriorityQueue) {
    neighbors.stream()
        .filter(neighbor -> !processedPoints.contains(neighbor))
        .forEach(neighbor -> {
          // reachability distance is max from min core radius and distance to cluster point
          var neighborReachabilityDistance = Math.max(
              pointToMinCoreRadius.get(neighbor), neighbor.distance(point));
          if (!pointToReachabilityDistance.containsKey(neighbor)) {
            var clusterDistance =
                new ClusterDistance(neighbor, neighborReachabilityDistance);
            nearestNeighborPriorityQueue.add(clusterDistance);
            pointToReachabilityDistance.put(neighbor, neighborReachabilityDistance);
          } else if (neighborReachabilityDistance < pointToReachabilityDistance.get(neighbor)) {
            var clusterDistance =
                new ClusterDistance(neighbor, neighborReachabilityDistance);
            nearestNeighborPriorityQueue.remove(clusterDistance);
            nearestNeighborPriorityQueue.add(clusterDistance);
            pointToReachabilityDistance.put(neighbor, neighborReachabilityDistance);
          }
        });
  }

  private static void validateCoreNumberOfNeighbors(int coreNumberOfNeighbors) {
    if (coreNumberOfNeighbors < 1) {
      throw new IllegalArgumentException(String.format(
          "Core number of neighbors should be positive: coreNumberOfNeighbors=%s",
          coreNumberOfNeighbors));
    }
  }

  private static void validateCoreRadius(double maxCoreRadius) {
    if (maxCoreRadius <= 0) {
      throw new IllegalArgumentException(String.format(
          "Core radius should be positive: maxCoreRadius=%s", maxCoreRadius));
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

  private static record ClusterDistance(
      Point point,
      double distance) implements Comparable<ClusterDistance> {

    @Override
    public int compareTo(ClusterDistance that) {
      return Double.compare(this.distance, that.distance);
    }
  }

  public static record OpticsClusterSearchResult(
      double maxCoreRadius,
      Point[] points,
      Map<Point, Double> pointToReachabilityDistance) {

    public int[] pointClusterIndexes(double coreRadius) {
      return clusterView(coreRadius).pointClusterIndexes();
    }

    public ClusterView clusterView(double coreRadius) {
      validateCoreRadius(coreRadius);

      var clusterIndex = -1;
      var clusterIndexIncremented = false;
      var pointToClusterIndex = new HashMap<Point, Integer>();

      Point prevPoint = null;
      for (Entry<Point, Double> entry : pointToReachabilityDistance.entrySet()) {
        var point = entry.getKey();
        var distance = entry.getValue();
        if (distance > coreRadius) {
          prevPoint = point;
          clusterIndexIncremented = true;
        } else {
          if (clusterIndexIncremented) {
            clusterIndexIncremented = false;
            pointToClusterIndex.put(prevPoint, ++clusterIndex);
          }
          pointToClusterIndex.put(point, clusterIndex);
        }
      }
      return new ClusterView() {

        @Override
        public Point[] points() {
          return points;
        }

        @Override
        public int[] pointClusterIndexes() {
          return Arrays.stream(points)
              .map(key -> pointToClusterIndex.getOrDefault(key, OUTLIER))
              .mapToInt(any -> any)
              .toArray();
        }

        @Override
        public Point[] clusterCentroids() {
          return new Point[0];
        }

        @Override
        public int outlierIndex() {
          return OUTLIER;
        }
      };
    }

    private void validateCoreRadius(double coreRadius) {
      if (coreRadius > maxCoreRadius) {
        throw new IllegalArgumentException(String.format(
            "Core radius should cannot be greater than max core radius: coreRadius=%s, maxCoreRadius=%s",
            coreRadius, maxCoreRadius));
      }
    }
  }
}