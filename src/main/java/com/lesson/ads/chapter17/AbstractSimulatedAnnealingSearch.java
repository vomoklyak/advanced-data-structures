package com.lesson.ads.chapter17;

import com.lesson.ads.util.Point;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractSimulatedAnnealingSearch {

  private final Random random;

  Point search(
      int maxNumberOfIterations,
      Point startPoint,
      double startTemperature,
      StepFunction stepFunction,
      AcceptanceFunction acceptanceFunction,
      TemperatureCoolingFunction coolingFunction) {
    var point = startPoint;
    var temperature = startTemperature;
    for (int iteration = 0; iteration < maxNumberOfIterations; iteration++) {
      temperature = coolingFunction.apply(temperature);
      var nextPoint = stepFunction.apply(point, temperature);
      point = acceptanceFunction.apply(point, nextPoint, temperature) > random.nextFloat(0, 1) ?
          nextPoint : point;
    }
    return point;
  }

  @FunctionalInterface
  interface StepFunction {

    Point apply(Point point, double temperature);
  }

  @FunctionalInterface
  interface AcceptanceFunction {

    double apply(Point startPoint, Point endPoint, double temperature);
  }

  @FunctionalInterface
  interface TemperatureCoolingFunction {

    double apply(double temperature);
  }
}