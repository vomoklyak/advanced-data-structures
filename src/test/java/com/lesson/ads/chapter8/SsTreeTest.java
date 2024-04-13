package com.lesson.ads.chapter8;

import com.lesson.ads.chapter8.SsTree.SsTreeNode;
import com.lesson.ads.util.Point;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SsTreeTest {

  @Test
  void shouldPutPoint() {
    // Given
    final var pointOne = new Point(0D, 0D);
    final var pointTwo = new Point(1D, 1D);
    final var sut = SsTree.create(2, 1, 3, pointOne);

    // When
    final var result = sut.putPoint(pointTwo);

    // Then
    Assertions.assertThat(result).isEqualTo(pointTwo);
    Assertions.assertThat(sut.asNodes())
        .returns(List.of(pointOne, pointTwo), SsTreeNode::getPoints)
        .returns(null, SsTreeNode::getChildren)
        .returns(true, SsTreeNode::isLeaf)
        .returns(new Point(0.5D, 0.5D), SsTreeNode::getCentroid)
        .returns(Math.sqrt(0.5D), SsTreeNode::getRadius);
  }

  @Test
  void shouldPutPointCaseEmptyTree() {
    // Given
    final var point = new Point(0D, 0D);
    final var sut = SsTree.create(2, 1, 3);

    // When
    final var result = sut.putPoint(point);

    // Then
    Assertions.assertThat(result).isEqualTo(point);
    Assertions.assertThat(sut.asNodes())
        .returns(List.of(point), SsTreeNode::getPoints)
        .returns(null, SsTreeNode::getChildren)
        .returns(true, SsTreeNode::isLeaf)
        .returns(point, SsTreeNode::getCentroid)
        .returns(0D, SsTreeNode::getRadius);
  }

  @Test
  void shouldPutPointCaseExistentPoint() {
    // Given
    final var point = new Point(0D, 0D);
    final var sut = SsTree.create(2, 1, 3, point);

    // When
    final var result = sut.putPoint(point);

    // Then
    Assertions.assertThat(result).isEqualTo(point);
    Assertions.assertThat(sut.asNodes())
        .returns(List.of(point), SsTreeNode::getPoints)
        .returns(null, SsTreeNode::getChildren)
        .returns(true, SsTreeNode::isLeaf)
        .returns(point, SsTreeNode::getCentroid)
        .returns(0D, SsTreeNode::getRadius);
  }

  @Test
  void shouldPutPointCaseSplit() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(-1D, 1D);
    final var pointThree = new Point(1D, -1D);
    final var pointFour = new Point(-1D, -1D);
    final var sut = SsTree.create(
        2, 1, 3, pointOne, pointTwo, pointThree);

    // When
    final var result = sut.putPoint(pointFour);

    // Then
    Assertions.assertThat(result).isEqualTo(pointFour);
    Assertions.assertThat(sut.asNodes())
        .returns(false, SsTreeNode::isLeaf)
        .returns(new Point(0D, 0D), SsTreeNode::getCentroid)
        .returns(2D, SsTreeNode::getRadius);
    Assertions.assertThat(sut.asNodes().getChildren().get(0))
        .returns(List.of(pointTwo, pointFour), SsTreeNode::getPoints)
        .returns(null, SsTreeNode::getChildren)
        .returns(true, SsTreeNode::isLeaf)
        .returns(new Point(-1D, 0D), SsTreeNode::getCentroid)
        .returns(1D, SsTreeNode::getRadius);
    Assertions.assertThat(sut.asNodes().getChildren().get(1))
        .returns(List.of(pointOne, pointThree), SsTreeNode::getPoints)
        .returns(null, SsTreeNode::getChildren)
        .returns(true, SsTreeNode::isLeaf)
        .returns(new Point(1D, 0D), SsTreeNode::getCentroid)
        .returns(1D, SsTreeNode::getRadius);
  }

  @Test
  void shouldPutPointCaseNearestNode() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(-1D, 1D);
    final var pointThree = new Point(1D, -1D);
    final var pointFour = new Point(-1D, -1D);
    final var pointFive = new Point(2D, 2D);
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour);

    // When
    final var result = sut.putPoint(pointFive);

    // Then
    Assertions.assertThat(result).isEqualTo(pointFive);
    Assertions.assertThat(sut.asNodes().getChildren().get(1))
        .returns(List.of(pointOne, pointThree, pointFive), SsTreeNode::getPoints);
  }

  @Test
  void shouldPutPointCaseChildSplit() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(-1D, 1D);
    final var pointThree = new Point(1D, -1D);
    final var pointFour = new Point(-1D, -1D);
    final var pointFive = new Point(2D, 2D);
    final var pointSix = new Point(3D, 3D);
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour, pointFive);

    // When
    final var result = sut.putPoint(pointSix);

    // Then
    Assertions.assertThat(result).isEqualTo(pointSix);
    Assertions.assertThat(sut.asNodes().getChildren().get(2))
        .returns(List.of(pointOne, pointFive, pointSix), SsTreeNode::getPoints)
        .returns(true, SsTreeNode::isLeaf);
  }

  @Test
  void shouldPutPointCaseInternalNodeSplit() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(2D, 2D);
    final var pointThree = new Point(3D, 3D);
    final var pointFour = new Point(4D, 4D);
    final var pointFive = new Point(5D, 5D);
    final var pointSix = new Point(6D, 6D);
    final var pointSeven = new Point(7D, 7D);
    final var pointEight = new Point(8D, 8D);
    final var pointNine = new Point(9D, 9D);
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour, pointFive, pointSix, pointSeven, pointEight);

    // When
    final var result = sut.putPoint(pointNine);

    // Then
    Assertions.assertThat(result).isEqualTo(pointNine);
    Assertions.assertThat(sut.asNodes().getChildren().get(0).getChildren().get(0))
        .returns(List.of(pointOne, pointTwo), SsTreeNode::getPoints);
    Assertions.assertThat(sut.asNodes().getChildren().get(0).getChildren().get(1))
        .returns(List.of(pointThree, pointFour), SsTreeNode::getPoints);
    Assertions.assertThat(sut.asNodes().getChildren().get(1).getChildren().get(0))
        .returns(List.of(pointFive, pointSix), SsTreeNode::getPoints);
    Assertions.assertThat(sut.asNodes().getChildren().get(1).getChildren().get(1))
        .returns(List.of(pointSeven, pointEight, pointNine), SsTreeNode::getPoints);
  }

  @Test
  void shouldGetPoint() {
    // Given
    final var point = new Point(0D, 0D);
    final var sut = SsTree.create(2, 1, 3, point);

    // When
    final var result = sut.getPoint(point);

    // Then
    Assertions.assertThat(result).get().isEqualTo(point);
  }

  @Test
  void shouldGetPointCaseNonExistentPoint() {
    // Given
    final var point = new Point(0D, 0D);
    final var sut = SsTree.create(2, 1, 3);

    // When
    final var result = sut.getPoint(point);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetNearestPoint() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(-1D, 1D);
    final var pointThree = new Point(1D, -1D);
    final var pointFour = new Point(-1D, -1D);
    final var pointFive = new Point(2D, 2D);
    final var pointSix = new Point(3D, 3D);
    final var pointSeven = new Point(2.6D, 2.6D);
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour, pointFive, pointSix);

    // When
    final var result = sut.getNearestPoint(pointSeven);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointSix);
  }

  @Test
  void shouldGetNearestPointCaseEmptyTree() {
    // Given
    final var point = new Point(1D, 1D);
    final var sut = SsTree.create(2, 1, 3);

    // When
    final var result = sut.getNearestPoint(point);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetNearestPointWithApproximation() {
    // Given
    final var pointOne = new Point(2D, 0D);
    final var pointTwo = new Point(3D, 0D);
    final var pointThree = new Point(7D, 0D);
    final var pointFour = new Point(0D, 0D);
    final var pointFive = new Point(0.9, 0D);
    final var approximationError = 0.3D;
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour);

    // When
    final var result = sut.getNearestPoint(pointFive, approximationError);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointOne);
  }

  @Test
  void shouldGetNearestPointWithApproximationCaseLowApproximationError() {
    // Given
    final var pointOne = new Point(2D, 0D);
    final var pointTwo = new Point(3D, 0D);
    final var pointThree = new Point(7D, 0D);
    final var pointFour = new Point(0D, 0D);
    final var pointFive = new Point(0.9, 0D);
    final var approximationError = 0.1D;
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour);

    // When
    final var result = sut.getNearestPoint(pointFive, approximationError);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointFour);
  }

  @Test
  void shouldGetNearestPointWithApproximationCaseEmptyTree() {
    // Given
    final var point = new Point(1D, 1D);
    final var approximationError = 0.1D;
    final var sut = SsTree.create(2, 1, 3);

    // When
    final var result = sut.getNearestPoint(point, approximationError);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetRegionPoints() {
    // Given
    final var pointOne = new Point(1D, 1D);
    final var pointTwo = new Point(-1D, 1D);
    final var pointThree = new Point(1D, -1D);
    final var pointFour = new Point(-1D, -1D);
    final var pointFive = new Point(2D, 2D);
    final var pointSix = new Point(3D, 3D);
    final var pointSeven = new Point(2.6D, 2.6D);
    final var radius = 1D;
    final var sut = SsTree.create(2, 1, 3,
        pointOne, pointTwo, pointThree, pointFour, pointFive, pointSix);

    // When
    final var result = sut.getRegionPoints(pointSeven, radius);

    // Then
    Assertions.assertThat(result).containsOnly(pointFive, pointSix);
  }

  @Test
  void shouldGetRegionPointsCaseEmptyTree() {
    // Given
    final var point = new Point(1D, 1D);
    final var radius = 1D;
    final var sut = SsTree.create(2, 1, 3);

    // When
    final var result = sut.getRegionPoints(point, radius);

    // Then
    Assertions.assertThat(result).isEmpty();
  }
}