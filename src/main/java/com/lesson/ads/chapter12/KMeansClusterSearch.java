package com.lesson.ads.chapter12;


import com.lesson.ads.chapter12.ClusterViewer.ClusterView;
import com.lesson.ads.chapter8.KdTree;
import com.lesson.ads.util.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KMeansClusterSearch {

  public static void main(String[] args) {
    var searchResult = new KMeansClusterSearch(2)
        .search(1000, 2, Datasets.spiral());
    ClusterViewer.view(searchResult);
  }

  private final int dimension;

  public KMeansClusterSearch(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public KMeansClusterSearchResult search(
      int maxNumberOfIterations, Point[] initialCentroids, Point... points) {
    validatePoints(dimension, points);
    var centroidToClusterIndex = IntStream.range(0, initialCentroids.length)
        .boxed()
        .collect(Collectors.toMap(index -> initialCentroids[index], Function.identity()));
    return search(maxNumberOfIterations, centroidToClusterIndex, points);
  }

  public KMeansClusterSearchResult search(
      int maxNumberOfIterations, int maxNumberOfClusters, Point... points) {
    validatePoints(dimension, points);
    validateNumberOfClusters(maxNumberOfClusters, points);
    var centroidToClusterIndex =
        initialClusterCentroidToClusterIndex(maxNumberOfClusters, points);
    return search(maxNumberOfIterations, centroidToClusterIndex, points);
  }

  private KMeansClusterSearchResult search(int maxNumberOfIterations,
      Map<Point, Integer> initialCentroidToClusterIndex, Point... points) {
    var iteration = 0;
    var affectedClusterIndexes = new HashSet<Integer>();
    var pointClusterIndexes = IntStream.range(0, points.length).map(any -> -1).toArray();
    while (true) {
      affectedClusterIndexes.clear();
      var centroidToClusterIndex = iteration++ == 0 ?
          initialCentroidToClusterIndex :
          clusterCentroidToClusterIndex(points, pointClusterIndexes);
      var centroidKdTree = new KdTree(
          dimension, centroidToClusterIndex.keySet().toArray(new Point[0]));

      IntStream.range(0, points.length).forEach(pointIndex -> {
        var currentPointClusterIndex = pointClusterIndexes[pointIndex];
        var newPointClusterIndex = centroidToClusterIndex.get(
            centroidKdTree.getNearestPoint(points[pointIndex]).orElseThrow());
        if (currentPointClusterIndex != newPointClusterIndex) {
          affectedClusterIndexes.add(currentPointClusterIndex);
          affectedClusterIndexes.add(newPointClusterIndex);
          pointClusterIndexes[pointIndex] = newPointClusterIndex;
        }
      });

      if (affectedClusterIndexes.isEmpty() || iteration > maxNumberOfIterations) {
        var centroids = centroidToClusterIndex.entrySet().stream()
            .sorted(Entry.comparingByValue())
            .map(Entry::getKey)
            .toArray(Point[]::new);
        return new KMeansClusterSearchResult(points, pointClusterIndexes, centroids, iteration);
      }
    }
  }

  private static void validateNumberOfClusters(int numberOfClusters, Point[] points) {
    if (numberOfClusters > points.length) {
      throw new IllegalArgumentException(String.format(
          "Number of clusters cannot exceed number of points: numberOfClusters=%s, numberOfPoints%s",
          numberOfClusters, points.length));
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

  private Map<Point, Integer> initialClusterCentroidToClusterIndex(
      int numberOfClusters, Point[] points) {
    var random = new Random();
    var clusterIndexer = new AtomicInteger(0);
    return IntStream.generate(() -> random.nextInt(points.length))
        // unique only point indexes
        .filter(new HashSet<>()::add)
        .limit(numberOfClusters)
        .mapToObj(pointIndex -> points[pointIndex])
        .map(point -> toPointWithRandomNoise(point, random))
        .collect(Collectors.toMap(
            Function.identity(), centroid -> clusterIndexer.getAndIncrement()));
  }

  private Point toPointWithRandomNoise(Point point, Random random) {
    var newCoordinates = IntStream.range(0, dimension)
        .mapToDouble(coordinate -> point.coordinate(coordinate) + random.nextDouble(1))
        .toArray();
    return new Point(newCoordinates);
  }

  private Map<Point, Integer> clusterCentroidToClusterIndex(
      Point[] points, int[] pointClusterIndexes) {
    var clusterIndexToNumberPoints = new HashMap<Integer, Integer>();
    var clusterIndexToCoordinateAccumulator = new HashMap<Integer, double[]>();

    IntStream.range(0, points.length).forEach(pointIndex -> {
      clusterIndexToNumberPoints.merge(pointClusterIndexes[pointIndex], 1, Integer::sum);
      var clusterCoordinateAccumulator = clusterIndexToCoordinateAccumulator
          .computeIfAbsent(pointClusterIndexes[pointIndex], key -> new double[dimension]);
      IntStream.range(0, dimension).forEach(coordinate ->
          clusterCoordinateAccumulator[coordinate] += points[pointIndex].coordinate(coordinate));
    });

    return clusterIndexToCoordinateAccumulator.entrySet().stream()
        .collect(Collectors.toMap(entry -> toMeanPoint(
            clusterIndexToNumberPoints.get(entry.getKey()), entry.getValue()), Entry::getKey));
  }

  private Point toMeanPoint(Integer numberOfPoints, double[] coordinateAccumulator) {
    var meanCoordinates = IntStream.range(0, dimension)
        .mapToDouble(coordinate -> (1D / numberOfPoints) * coordinateAccumulator[coordinate])
        .toArray();
    return new Point(meanCoordinates);
  }

  public static record KMeansClusterSearchResult(
      Point[] points,
      int[] pointClusterIndexes,
      Point[] clusterCentroids,
      int numberOfIterations) implements ClusterView {

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
      return clusterCentroids;
    }

    @Override
    public int outlierIndex() {
      return -1;
    }
  }
}