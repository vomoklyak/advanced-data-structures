package com.lesson.ads.chapter8;

import com.lesson.ads.chapter8.KdTree.KdTreeNode;
import com.lesson.ads.util.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class KdTreeTest {

  @Test
  void shouldPutPoint() {
    // Given
    final var point = new Point(0, 0);
    final var sut = new KdTree(2);

    // When
    final var result = sut.putPoint(point);

    // Then
    Assertions.assertThat(result).isEqualTo(point);
    Assertions.assertThat(sut.asNodes())
        .returns(point, KdTreeNode::getPoint)
        .returns(0, KdTreeNode::getLevel)
        .returns(null, KdTreeNode::getLeft)
        .returns(null, KdTreeNode::getRight);
  }

  @Test
  void shouldPutPointCaseExistentPoint() {
    // Given
    final var point = new Point(0, 0);
    final var sut = new KdTree(2);
    sut.putPoint(point);

    // When
    final var result = sut.putPoint(point);

    // Then
    Assertions.assertThat(result).isEqualTo(point);
    Assertions.assertThat(sut.asNodes())
        .returns(point, KdTreeNode::getPoint)
        .returns(0, KdTreeNode::getLevel)
        .returns(null, KdTreeNode::getLeft)
        .returns(null, KdTreeNode::getRight);
  }

  @Test
  void shouldPutPointCaseSplitting() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(-1, -1);
    final var pointThree = new Point(2, 2);
    final var pointFour = new Point(3, 1);
    final var pointFive = new Point(4, 3);
    final var sut = new KdTree(2);

    // When
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);
    sut.putPoint(pointThree);
    sut.putPoint(pointFour);
    sut.putPoint(pointFive);

    // Then
    Assertions.assertThat(sut.asNodes())
        .returns(pointOne, KdTreeNode::getPoint)
        .returns(0, KdTreeNode::getLevel);
    Assertions.assertThat(sut.asNodes().getLeft())
        .returns(pointTwo, KdTreeNode::getPoint)
        .returns(1, KdTreeNode::getLevel);
    Assertions.assertThat(sut.asNodes().getRight())
        .returns(pointThree, KdTreeNode::getPoint)
        .returns(1, KdTreeNode::getLevel);
    Assertions.assertThat(sut.asNodes().getRight().getLeft())
        .returns(pointFour, KdTreeNode::getPoint)
        .returns(2, KdTreeNode::getLevel);
    Assertions.assertThat(sut.asNodes().getRight().getRight())
        .returns(pointFive, KdTreeNode::getPoint)
        .returns(2, KdTreeNode::getLevel);
  }

  @Test
  void shouldGetPoint() {
    // Given
    final var point = new Point(0, 0);
    final var sut = new KdTree(2, point);

    // When
    final var result = sut.getPoint(point);

    // Then
    Assertions.assertThat(result).get().isEqualTo(point);
  }

  @Test
  void shouldGetPointCaseNonExistentPoint() {
    // Given
    final var point = new Point(0, 0);
    final var sut = new KdTree(2);

    // When
    final var result = sut.getPoint(point);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetPointCaseLeftChild() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(-1, -1);
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);

    // When
    final var result = sut.getPoint(pointTwo);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointTwo);
  }

  @Test
  void shouldGetPointCaseRightChild() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 1);
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);

    // When
    final var result = sut.getPoint(pointTwo);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointTwo);
  }

  @Test
  void shouldGetNearestPoint() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 1);
    final var pointThree = new Point(4, 4);
    final var point = new Point(3, 3);
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);
    sut.putPoint(pointThree);

    // When
    final var result = sut.getNearestPoint(point);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointThree);
  }

  @Test
  void shouldGetNearestPointCaseSplittingLine() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 2);
    final var pointThree = new Point(4, 0.5);
    final var pointFour = new Point(10, 3);
    final var point = new Point(9, 1);
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);
    sut.putPoint(pointThree);
    sut.putPoint(pointFour);

    // When
    final var result = sut.getNearestPoint(point);

    // Then
    Assertions.assertThat(result).get().isEqualTo(pointFour);
  }

  @Test
  void shouldGetRegionPoints() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 1);
    final var pointThree = new Point(4, 4);
    final var point = new Point(3, 3);
    final var radius = 3;
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);
    sut.putPoint(pointThree);

    // When
    final var result = sut.getRegionPoints(point, radius);

    // Then
    Assertions.assertThat(result).containsOnly(pointTwo, pointThree);
  }

  @Test
  void shouldGetRegionPointsCaseSplittingLine() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 2);
    final var pointThree = new Point(4, 0.5);
    final var pointFour = new Point(10, 3);
    final var point = new Point(9, 1);
    final var radius = point.distance(pointFour);
    final var sut = new KdTree(2);
    sut.putPoint(pointOne);
    sut.putPoint(pointTwo);
    sut.putPoint(pointThree);
    sut.putPoint(pointFour);

    // When
    final var result = sut.getRegionPoints(point, radius);

    // Then
    Assertions.assertThat(result).containsOnly(pointFour);
  }

  @Test
  void shouldCreateBalancedKdTree() {
    // Given
    final var pointOne = new Point(0, 0);
    final var pointTwo = new Point(1, 1);
    final var pointThree = new Point(4, 4);

    // When
    final var result = new KdTree(2, pointOne, pointTwo, pointThree);

    // Then
    Assertions.assertThat(result.asNodes())
        .returns(pointTwo, KdTreeNode::getPoint)
        .returns(0, KdTreeNode::getLevel);
    Assertions.assertThat(result.asNodes().getLeft())
        .returns(pointOne, KdTreeNode::getPoint)
        .returns(1, KdTreeNode::getLevel);
    Assertions.assertThat(result.asNodes().getRight())
        .returns(pointThree, KdTreeNode::getPoint)
        .returns(1, KdTreeNode::getLevel);
  }
}