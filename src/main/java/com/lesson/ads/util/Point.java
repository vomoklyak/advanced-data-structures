package com.lesson.ads.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Point {

  private final double[] coordinates;

  public Point(double... coordinates) {
    validateCoordinates(coordinates);
    this.coordinates = coordinates;
  }

  private static void validateCoordinates(double[] coordinates) {
    if (coordinates == null) {
      throw new IllegalArgumentException("Coordinates cannot be null");
    }
  }

  public int dimension() {
    return coordinates.length;
  }

  public double coordinate(int index) {
    return coordinates[index];
  }

  public DoubleStream coordinateStream() {
    return DoubleStream.of(coordinates);
  }

  public Optional<Point> nearest(Point... points) {
    return nearestPoint(this, points);
  }

  public double distance(Point point) {
    return distance(this, point);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.coordinates);
  }

  @Override
  public boolean equals(Object that) {
    return that != null &&
        that.getClass() == this.getClass() &&
        Arrays.equals(((Point) that).coordinates, this.coordinates);
  }

  @Override
  public String toString() {
    return String.format("Point(%s)", Arrays.toString(coordinates));
  }

  public static Optional<Point> nearestPoint(Point targetPoint, Point[] points) {
    var nearestPoint = (Point) null;
    var minDistance = Double.POSITIVE_INFINITY;
    for (Point point : points) {
      var distance = distance(targetPoint, point);
      if (distance < minDistance) {
        minDistance = distance;
        nearestPoint = point;
      }
    }
    return Optional.ofNullable(nearestPoint);
  }

  public static double distance(Point left, Point right) {
    validateDimensions(left, right);
    return IntStream.range(0, left.dimension())
        .mapToObj(index -> Math.pow(left.coordinate(index) - right.coordinate(index), 2))
        .collect(Collectors.collectingAndThen(Collectors.summingDouble(any -> any), Math::sqrt));
  }

  private static void validateDimensions(Point left, Point right) {
    if (left.dimension() != right.dimension()) {
      throw new IllegalArgumentException(String.format(
          "Point dimensions cannot differ: left=%s, right=%s",
          left.dimension(), right.dimension()));
    }
  }
}
