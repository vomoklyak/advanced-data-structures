package com.lesson.ads.chapter8;

import com.lesson.ads.util.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class KdTree {

  private final int dimension;
  private KdTreeNode root;

  public KdTree(int dimension) {
    validateDimension(dimension);
    this.dimension = dimension;
  }

  private static void validateDimension(int dimension) {
    if (dimension < 1) {
      throw new IllegalArgumentException(String.format(
          "Dimension should be positive: dimension=%s", dimension));
    }
  }

  public KdTree(int dimension, Point... points) {
    this(dimension);
    Stream.ofNullable(points)
        .flatMap(Arrays::stream).forEach(point -> validatePoint(dimension, point));
    this.root = balancedNode(dimension, 0, points.clone(), 0, points.length - 1);
  }

  private static KdTreeNode balancedNode(
      int dimension, int level, Point[] points, int startIndex, int endIndex) {
    if (endIndex - startIndex < 0) {
      return null;
    }
    if (endIndex - startIndex == 0) {
      return new KdTreeNode(level, points[startIndex]);
    }
    var medianPointIndex =
        medianPointIndex(level % dimension, points, startIndex, endIndex);
    var medianNode = new KdTreeNode(level, points[medianPointIndex]);
    medianNode.left =
        balancedNode(dimension, level + 1, points, startIndex, medianPointIndex - 1);
    medianNode.right =
        balancedNode(dimension, level + 1, points, medianPointIndex + 1, endIndex);
    return medianNode;
  }

  private static int medianPointIndex(
      int coordinate, Point[] points, int startIndex, int endIndex) {
    var median = (startIndex + endIndex) / 2;
    Comparator<Point> comparator = Comparator.comparing(point -> point.coordinate(coordinate));
    ArrayOrderStatistic.selectKth(points, startIndex, endIndex, comparator, median);
    return median;
  }

  public Point putPoint(Point point) {
    validatePoint(dimension, point);
    root = putPoint(root, 0, point);
    return point;
  }

  private KdTreeNode putPoint(KdTreeNode node, int level, Point point) {
    if (node == null) {
      return new KdTreeNode(level, point);
    }
    var coordinate = node.coordinate();
    if (node.point.equals(point)) {
      return node;
    } else if (point.coordinate(coordinate) < node.point.coordinate(coordinate)) {
      node.left = putPoint(node.left, level + 1, point);
      return node;
    } else {
      node.right = putPoint(node.right, level + 1, point);
      return node;
    }
  }

  public Optional<Point> getPoint(Point point) {
    validatePoint(dimension, point);
    return nodeOpt(root, 0, point)
        .map(node -> node.point);
  }

  private Optional<KdTreeNode> nodeOpt(KdTreeNode node, int level, Point point) {
    if (node == null) {
      return Optional.empty();
    } else {
      var coordinate = node.coordinate();
      if (node.point.equals(point)) {
        return Optional.of(node);
      } else if (point.coordinate(coordinate) < node.point.coordinate(coordinate)) {
        return nodeOpt(node.left, level + 1, point);
      } else {
        return nodeOpt(node.right, level + 1, point);
      }
    }
  }

  public Optional<Point> getNearestPoint(Point point) {
    validatePoint(dimension, point);
    return Optional.ofNullable(root)
        .map(root -> getNearestPoint(root, point, root.point));
  }

  private Point getNearestPoint(KdTreeNode node, Point point, Point nearestPoint) {
    if (node != null) {
      var coordinate = node.coordinate();
      var nearbyLeftNode = point.coordinate(coordinate) < node.point.coordinate(coordinate);
      var nearbyNode = nearbyLeftNode ? node.left : node.right;
      var distantNode = nearbyLeftNode ? node.right : node.left;
      nearestPoint = point.nearest(node.point,
          getNearestPoint(nearbyNode, point, nearestPoint)).orElseThrow();
      var splittingLineDistance =
          Math.abs(point.coordinate(coordinate) - node.point.coordinate(coordinate));
      var minDistance = point.distance(nearestPoint);
      if (splittingLineDistance < minDistance) {
        nearestPoint = point.nearest(nearestPoint,
            getNearestPoint(distantNode, point, nearestPoint)).orElseThrow();
      }
    }
    return nearestPoint;
  }

  public Set<Point> getRegionPoints(Point point, double radius) {
    validatePoint(dimension, point);
    validateRadius(radius);
    var regionPoints = new HashSet<Point>();
    getRegionPoints(root, point, radius, regionPoints);
    return regionPoints;
  }

  private void getRegionPoints(
      KdTreeNode node, Point point, double radius, Set<Point> regionPoints) {
    if (node != null) {
      var coordinate = node.coordinate();
      var nearbyLeftNode = point.coordinate(coordinate) < node.point.coordinate(coordinate);
      var nearbyNode = nearbyLeftNode ? node.left : node.right;
      var distantNode = nearbyLeftNode ? node.right : node.left;
      if (point.distance(node.point) <= radius) {
        regionPoints.add(node.point);
      }
      getRegionPoints(nearbyNode, point, radius, regionPoints);
      var splittingLineDistance =
          Math.abs(point.coordinate(coordinate) - node.point.coordinate(coordinate));
      if (splittingLineDistance <= radius) {
        getRegionPoints(distantNode, point, radius, regionPoints);
      }
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

  public KdTreeNode asNodes() {
    return root == null ? null : root.copy();
  }

  @Getter
  @RequiredArgsConstructor
  static class KdTreeNode {

    private final int level;
    private final Point point;

    private KdTreeNode left;
    private KdTreeNode right;

    int coordinate() {
      return level % point.dimension();
    }

    KdTreeNode copy() {
      var node = new KdTreeNode(level, point);
      if (left != null) {
        node.left = this.left.copy();
      }
      if (right != null) {
        node.right = this.right.copy();
      }
      return node;
    }
  }
}