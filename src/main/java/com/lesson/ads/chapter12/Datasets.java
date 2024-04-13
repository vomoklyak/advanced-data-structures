package com.lesson.ads.chapter12;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.lesson.ads.util.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("SuspiciousToArrayCall")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Datasets {

  public static Point[] spiral() {
    return spiral(6 * Math.PI, Math.toRadians(1));
  }

  public static Point[] spiral(double maxDegreeRadian, double deltaDegreeRadian) {
    var points = new ArrayList<>();
    var degreeRadian = 0D;
    while (degreeRadian <= maxDegreeRadian) {
      var r = 15 * degreeRadian;
      var axesX = r * cos(degreeRadian);
      var axesY = r * sin(degreeRadian);
      points.add(new Point(axesX, axesY));
      degreeRadian += deltaDegreeRadian;
    }
    return points.toArray(new Point[0]);
  }

  public static Point[] concentricCircles() {
    return concentricCircles(100D, 200D, 0.5D);
  }

  public static Point[] concentricCircles(double innerRadius, double outerRadius, double delta) {
    return Stream.of(
            circle(innerRadius, delta),
            circle(outerRadius, delta)
        )
        .flatMap(Collection::stream)
        .toList()
        .toArray(new Point[0]);
  }

  private static ArrayList<Point> circle(double radius, double delta) {
    var points = new ArrayList<Point>();
    var axesY = 0D;
    var axesX = -radius;
    while (axesX <= radius) {
      axesY = Math.sqrt(radius * radius - axesX * axesX);
      points.add(new Point(axesX, axesY));
      points.add(new Point(axesX, -axesY));
      axesX += delta;
    }
    return points;
  }

  public static Point[] lines() {
    return parallelLines(0.1D, 450D, 1D);
  }

  public static Point[] parallelLines() {
    return parallelLines(0, 450D, 1D);
  }

  public static Point[] parallelLines(double gradient, double length, double delta) {
    var points = new ArrayList<>();
    var axesX = -length / 2;
    while (axesX <= length / 2) {
      points.add(new Point(axesX, 20));
      points.add(new Point(axesX, gradient * axesX - 20));
      axesX += delta;
    }
    return points.toArray(new Point[0]);
  }

  public static Point[] heterogeneousDensitySquares() {
    return Stream.of(
            square(10D, 10D, 20D, 20D, 1D),
            square(21D, 21D, 31D, 31D, 1D),
            square(50D, 50D, 70D, 70D, 2D),
            square(200D, 200D, 230D, 230D, 3D)
        )
        .flatMap(Collection::stream)
        .toList()
        .toArray(new Point[0]);
  }

  private static ArrayList<Point> square(
      double leftMostAxesX, double leftMostAxesY,
      double rightMostAxesX, double rightMostAxesY, double delta) {
    var points = new ArrayList<Point>();
    var axesX = leftMostAxesX;
    var axesY = leftMostAxesY;
    while (axesX <= rightMostAxesX) {
      while (axesY <= rightMostAxesY) {
        points.add(new Point(axesX, axesY));
        axesY += delta;
      }
      axesY = leftMostAxesY;
      axesX += delta;
    }
    return points;
  }
}
