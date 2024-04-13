package com.lesson.ads.chapter8;

import com.lesson.ads.util.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SsTree {

  // k
  private final int dimension;
  // m
  private final int minBranchingFactor;
  // M
  private final int maxBranchingFactor;
  private SsTreeNode root;

  private SsTree(int dimension, int minBranchingFactor, int maxBranchingFactor) {
    validateDimension(dimension);
    validateBranchingFactor(minBranchingFactor, maxBranchingFactor);
    this.dimension = dimension;
    this.minBranchingFactor = minBranchingFactor;
    this.maxBranchingFactor = maxBranchingFactor;
  }

  public static SsTree create(int dimension, int minBranchingFactor, int maxBranchingFactor) {
    return new SsTree(dimension, minBranchingFactor, maxBranchingFactor);
  }

  public static SsTree create(
      int dimension, int minBranchingFactor, int maxBranchingFactor, Point... points) {
    var tree = new SsTree(dimension, minBranchingFactor, maxBranchingFactor);
    Stream.ofNullable(points).flatMap(Arrays::stream).forEach(tree::putPoint);
    return tree;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  private static void validateBranchingFactor(int minBranchingFactor, int maxBranchingFactor) {
    if (minBranchingFactor < 1 || maxBranchingFactor < 1) {
      throw new IllegalArgumentException(String.format(
          "Branching factor should be positive: minBranchingFactor=%s, maxBranchingFactor=%s",
          minBranchingFactor, maxBranchingFactor));
    }
    if (minBranchingFactor > maxBranchingFactor / 2) {
      throw new IllegalArgumentException(String.format(
          "Branching factor should satisfy condition minBranchingFactor <= maxBranchingFactor/2: minBranchingFactor=%s, maxBranchingFactor=%s",
          minBranchingFactor, maxBranchingFactor));
    }
  }

  public Point putPoint(Point point) {
    validatePoint(dimension, point);
    if (root == null) {
      root = SsTreeNode.createLeafNode(dimension, List.of(point));
    } else {
      var newChildren = putPoint(root, point);
      if (!newChildren.isEmpty()) {
        // increase ss tree height
        root = SsTreeNode.createInternalNode(dimension, newChildren);
      }
    }
    return point;
  }

  private List<SsTreeNode> putPoint(SsTreeNode node, Point point) {
    if (node.leaf) {
      // leaf node
      if (!node.contain(point)) {
        node.add(point);
      }
    } else {
      // internal node
      var nearestChild = node.nearestChild(point);
      var newChildren = putPoint(nearestChild, point);
      if (!newChildren.isEmpty()) {
        node.delete(nearestChild);
        newChildren.forEach(node::add);
      }
    }
    node.updateBoundingHypersphere();
    return node.splitIfRequired(minBranchingFactor, maxBranchingFactor);
  }

  public Optional<Point> getPoint(Point point) {
    return getNearestPoint(point)
        .filter(point::equals);
  }

  public Optional<Point> getNearestPoint(Point point) {
    validatePoint(dimension, point);
    return Optional.ofNullable(root)
        .map(root -> getNearestPoint(root, point, 0D, infinityPoint()));
  }

  private Point infinityPoint() {
    var coordinates = IntStream.range(0, dimension)
        .mapToDouble(any -> Double.MAX_VALUE)
        .toArray();
    return new Point(coordinates);
  }

  public Optional<Point> getNearestPoint(Point point, double approximationError) {
    validatePoint(dimension, point);
    validateApproximationError(approximationError);
    return Optional.ofNullable(root)
        .map(root -> getNearestPoint(root, point, approximationError, infinityPoint()));
  }

  private void validateApproximationError(double approximationError) {
    if (approximationError < 0 || approximationError > 0.5D) {
      throw new IllegalArgumentException(String.format(
          "Approximation error should belong to range [0,0.5]: approximationError=%s",
          approximationError));
    }
  }

  private Point getNearestPoint(
      SsTreeNode node, Point point, double approximationError, Point nearestPoint) {
    if (node.leaf) {
      return point.nearest(nearestPoint, node.nearestPoint(point)).orElseThrow();
    } else {
      var sortedChildren = node.children.stream()
          .sorted(Comparator.comparing(child -> child.outerBoundingHypersphereDistance(point)))
          .toList();
      for (SsTreeNode child : sortedChildren) {
        var approximatedNearestPointDistance =
            point.distance(nearestPoint) / (1 + approximationError);
        if (child.outerBoundingHypersphereDistance(point) < approximatedNearestPointDistance) {
          nearestPoint = getNearestPoint(child, point, approximationError, nearestPoint);
        }
      }
    }
    return nearestPoint;
  }

  public Set<Point> getRegionPoints(Point point, double radius) {
    validatePoint(dimension, point);
    validateRadius(radius);
    var regionPoints = new HashSet<Point>();
    if (!empty()) {
      getRegionPoints(root, point, radius, regionPoints);
    }
    return regionPoints;
  }

  private void getRegionPoints(
      SsTreeNode node, Point point, double radius, Set<Point> regionPoints) {
    if (node.leaf) {
      node.points.stream()
          .filter(leafPoint -> point.distance(leafPoint) <= radius)
          .forEach(regionPoints::add);
    } else {
      node.children.stream()
          .filter(child -> child.outerBoundingHypersphereDistance(point) <= radius)
          .forEach(child -> getRegionPoints(child, point, radius, regionPoints));
    }
  }

  private void validateRadius(double radius) {
    if (radius <= 0) {
      throw new IllegalArgumentException(String.format(
          "Radius should be positive: radius=%s", radius));
    }
  }

  private void validatePoint(int dimension, Point point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    if (point.dimension() != dimension) {
      throw new IllegalArgumentException(String.format(
          "Point and tree dimensions cannot differ: point=%s, tree=%s",
          point.dimension(), dimension));
    }
  }

  public boolean empty() {
    return root == null;
  }

  public SsTreeNode asNodes() {
    return root == null ? null : root.copy();
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class SsTreeNode {

    private List<Point> points;
    private List<SsTreeNode> children;
    private boolean leaf;
    private Point centroid;
    private double radius;

    static SsTreeNode createLeafNode(int dimension, List<Point> points) {
      var node = new SsTreeNode();
      node.points = new ArrayList<>(points);
      node.leaf = true;
      node.centroid = centroid(dimension, node.points());
      node.radius = leafRadius(points, node.centroid);
      return node;
    }

    static SsTreeNode createInternalNode(int dimension, List<SsTreeNode> children) {
      var node = new SsTreeNode();
      node.children = new ArrayList<>(children);
      node.leaf = false;
      node.centroid = centroid(dimension, node.points());
      node.radius = internalNodeRadius(node.children, node.centroid);
      return node;
    }

    @Override
    public String toString() {
      return String.format(
          "Node(centroid=%s,radius=%s,size=%s)", centroid, radius, points().size());
    }

    int dimension() {
      return centroid.dimension();
    }

    void add(Point point) {
      points.add(point);
    }

    void add(SsTreeNode child) {
      children.add(child);
    }

    void delete(SsTreeNode child) {
      children.remove(child);
    }

    Point nearestPoint(Point point) {
      return points.stream()
          .min(Comparator.comparing(pint -> pint.distance(point)))
          .orElseThrow();
    }

    SsTreeNode nearestChild(Point point) {
      return children.stream()
          .min(Comparator.comparing(child -> child.centroid.distance(point)))
          .orElseThrow();
    }

    boolean contain(Point point) {
      return points.contains(point);
    }

    double outerBoundingHypersphereDistance(Point point) {
      // if point lies inside hypersphere distance is 0
      return Math.max(centroid.distance(point) - radius, 0);
    }

    void updateBoundingHypersphere() {
      centroid = centroid(centroid.dimension(), points());
      radius = leaf ? leafRadius(points, centroid) : internalNodeRadius(children, centroid);
    }

    List<SsTreeNode> splitIfRequired(int minBranchingFactor, int maxBranchingFactor) {
      if (size() > maxBranchingFactor) {
        var maxVarianceCoordinate = maxVarianceCoordinate();
        if (leaf) {
          points.sort(Comparator.comparing(
              point -> point.coordinate(maxVarianceCoordinate)));
          var minVarianceSplitIndex =
              minVarianceSplitIndex(minBranchingFactor, maxVarianceCoordinate);
          return List.of(
              SsTreeNode.createLeafNode(
                  dimension(), points.subList(0, minVarianceSplitIndex)),
              SsTreeNode.createLeafNode(
                  dimension(), points.subList(minVarianceSplitIndex, points.size()))
          );
        } else {
          children.sort(Comparator.comparing(
              child -> child.centroid.coordinate(maxVarianceCoordinate)));
          var minVarianceSplitIndex =
              minVarianceSplitIndex(minBranchingFactor, maxVarianceCoordinate);
          return List.of(
              SsTreeNode.createInternalNode(
                  dimension(), children.subList(0, minVarianceSplitIndex)),
              SsTreeNode.createInternalNode(
                  dimension(), children.subList(minVarianceSplitIndex, children.size()))
          );
        }
      }
      return Collections.emptyList();
    }

    int size() {
      return leaf ? points.size() : children.size();
    }

    SsTreeNode copy() {
      return leaf ?
          createLeafNode(dimension(), points.stream().toList()) :
          createInternalNode(dimension(), children.stream().map(SsTreeNode::copy).toList());
    }

    // axes with max variance (coordinate)
    private int maxVarianceCoordinate() {
      return IntStream.range(0, dimension()).boxed()
          .max(Comparator.comparing(coordinate -> variance(coordinate, points()))).orElseThrow();
    }

    // point/child split index with min variance (element index)
    private int minVarianceSplitIndex(int minBranchingFactor, int maxVarianceCoordinate) {
      var points = points();
      Function<Integer, Double> splitVariance = splitIndex ->
          variance(maxVarianceCoordinate, points.subList(0, splitIndex)) +
              variance(maxVarianceCoordinate, points.subList(splitIndex, points.size()));
      // ss tree invariant after split each node should have at least m points
      return IntStream.range(minBranchingFactor, points.size() - minBranchingFactor).boxed()
          .min(Comparator.comparing(splitVariance))
          .orElseThrow();
    }

    // leaf points, or internal node child centroids
    private List<Point> points() {
      return leaf ? points : children.stream().map(child -> child.centroid).toList();
    }

    private static Point centroid(int dimension, List<Point> points) {
      double[] centroidCoordinates = new double[dimension];
      IntStream.range(0, dimension).boxed()
          .forEach(coordinate -> centroidCoordinates[coordinate] = mean(coordinate, points));
      return new Point(centroidCoordinates);
    }

    private static double leafRadius(List<Point> points, Point centroid) {
      return points.stream()
          .mapToDouble(centroid::distance)
          .max()
          .orElseThrow();
    }

    private static double internalNodeRadius(List<SsTreeNode> children, Point centroid) {
      return children.stream()
          .mapToDouble(child -> centroid.distance(child.centroid) + child.radius)
          .max()
          .orElseThrow();
    }

    private static double mean(int coordinate, List<Point> points) {
      return (1D / points.size()) * points.stream()
          .mapToDouble(point -> point.coordinate(coordinate)).sum();
    }

    private static double variance(int coordinate, List<Point> points) {
      var mean = mean(coordinate, points);
      return (1D / points.size()) * points.stream()
          .mapToDouble(point -> Math.pow(point.coordinate(coordinate) - mean, 2)).sum();
    }
  }
}
