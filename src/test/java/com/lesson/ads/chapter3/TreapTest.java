package com.lesson.ads.chapter3;

import com.lesson.ads.chapter3.Treap.Entry;
import com.lesson.ads.chapter3.Treap.TreapNode;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class TreapTest {

  @Test
  void shouldGetHeight() {
    // Given
    final var sut = masterMinTreap();

    // When
    final var result = sut.height();

    // Then
    Assertions.assertThat(result).isEqualTo(4);
  }

  @Test
  void shouldGetElement() {
    // Given
    final var key = "K5";
    final var sut = masterMinTreap();

    // When
    final var result = sut.getElement(key);

    // Then
    Assertions.assertThat(result).get()
        .returns(key, Entry::key)
        .returns(1, Entry::priority);
  }

  @Test
  void shouldGetElementCaseNonExistentKey() {
    // Given
    final var key = "K";
    final var sut = masterMinTreap();

    // When
    final var result = sut.getElement(key);

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetMinKeyElement() {
    // Given
    final var sut = masterMinTreap();

    // When
    final var result = sut.getMinKeyElement();

    // Then
    Assertions.assertThat(result).get()
        .returns("K1", Entry::key)
        .returns(8, Entry::priority);
  }

  @Test
  void shouldGetMinKeyElementCaseEmptyTreap() {
    // Given
    final var sut = new Treap<String, Integer>();

    // When
    final var result = sut.getMinKeyElement();

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetMaxKeyElement() {
    // Given
    final var sut = masterMinTreap();

    // When
    final var result = sut.getMaxKeyElement();

    // Then
    Assertions.assertThat(result).get()
        .returns("K9", Entry::key)
        .returns(9, Entry::priority);
  }

  @Test
  void shouldGetMaxKeyElementCaseEmptyTreap() {
    // Given
    final var sut = new Treap<String, Integer>();

    // When
    final var result = sut.getMaxKeyElement();

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldGetTopPriorityElement() {
    // Given
    final var sut = masterMinTreap();

    // When
    final var result = sut.getTopPriorityElement();

    // Then
    Assertions.assertThat(result).get()
        .returns("K5", Entry::key)
        .returns(1, Entry::priority);
  }

  @Test
  void shouldGetTopPriorityElementCaseReversedComparator() {
    // Given
    final var sut = masterMaxTreap();

    // When
    final var result = sut.getTopPriorityElement();

    // Then
    Assertions.assertThat(result).get()
        .returns("K9", Entry::key)
        .returns(9, Entry::priority);
  }

  @Test
  void shouldGetTopPriorityElementCaseEmptyTreap() {
    // Given
    final var sut = new Treap<String, Integer>();

    // When
    final var result = sut.getTopPriorityElement();

    // Then
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldPut() {
    // Given
    final var keyOne = "K2";
    final var priorityOne = 1;
    final var keyTwo = "K1";
    final var priorityTwo = 2;
    final var keyThree = "K3";
    final var priorityThree = 3;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyTwo, node -> node.getLeft().getKey())
        .returns(keyThree, node -> node.getRight().getKey())
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityTwo, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getRight())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePopUpRightRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K3";
    final var priorityThree = 3;
    final var keyFour = "K1";
    final var priorityFour = 1;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyFour, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyTwo, node -> node.getRight().getKey())
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityTwo, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyThree, node -> node.getRight().getKey())
        .returns(keyFour, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight().getRight())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyTwo, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePopUpLeftRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 3;
    final var keyFour = "K3";
    final var priorityFour = 1;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyFour, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(keyTwo, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityTwo, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyFour, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft().getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyTwo, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePriorityUpdatePopUpLeftRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 3;
    final var keyFour = "K3";
    final var priorityFour = 4;
    final var priorityFive = 1;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    sut.put(keyFour, priorityFive);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyFour, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFive, TreapNode::getPriority)
        .returns(keyTwo, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityTwo, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyFour, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft().getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyTwo, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePriorityUpdatePopUpRightRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 3;
    final var keyFour = "K3";
    final var priorityFour = 4;
    final var priorityFive = 1;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    sut.put(keyThree, priorityFive);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityFive, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyTwo, node -> node.getRight().getKey())
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityTwo, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyFour, node -> node.getRight().getKey())
        .returns(keyThree, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight().getRight())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyTwo, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePriorityUpdatePushDownLeftRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 3;
    final var keyFour = "K3";
    final var priorityFour = 4;
    final var priorityFive = 5;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    sut.put(keyTwo, priorityFive);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyFour, node -> node.getRight().getKey())
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(keyTwo, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyThree, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getRight().getLeft())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityFive, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyFour, node -> node.getParent().getKey());
  }

  @Test
  void shouldPutCasePriorityUpdatePushDownRightRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 4;
    final var keyFour = "K3";
    final var priorityFour = 3;
    final var priorityFive = 5;
    final var sut = new Treap<String, Integer>();

    // When
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);
    sut.put(keyTwo, priorityFive);
    final var result = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyFour, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(result.getLeft())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyTwo, node -> node.getRight().getKey())
        .returns(keyFour, node -> node.getParent().getKey());
    Assertions.assertThat(result.getLeft().getLeft().getRight())
        .returns(keyTwo, TreapNode::getKey)
        .returns(priorityFive, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyThree, node -> node.getParent().getKey());
  }

  @Test
  void shouldDeleteCasePushDownRightRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 4;
    final var keyFour = "K3";
    final var priorityFour = 3;
    final var sut = new Treap<String, Integer>();
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);

    // When
    final var result = sut.delete(keyTwo);
    final var tree = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyTwo, Entry::key)
        .returns(priorityTwo, Entry::priority);
    Assertions.assertThat(tree)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyFour, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(tree.getLeft())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(tree.getLeft().getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyFour, node -> node.getParent().getKey());
  }

  @Test
  void shouldDeleteCasePushDownLeftRotation() {
    // Given
    final var keyOne = "K4";
    final var priorityOne = 1;
    final var keyTwo = "K2";
    final var priorityTwo = 2;
    final var keyThree = "K1";
    final var priorityThree = 3;
    final var keyFour = "K3";
    final var priorityFour = 4;
    final var sut = new Treap<String, Integer>();
    sut.put(keyOne, priorityOne);
    sut.put(keyTwo, priorityTwo);
    sut.put(keyThree, priorityThree);
    sut.put(keyFour, priorityFour);

    // When
    final var result = sut.delete(keyTwo);
    final var tree = sut.asNodes();

    // Then
    Assertions.assertThat(result)
        .returns(keyTwo, Entry::key)
        .returns(priorityTwo, Entry::priority);
    Assertions.assertThat(tree)
        .returns(keyOne, TreapNode::getKey)
        .returns(priorityOne, TreapNode::getPriority)
        .returns(keyThree, node -> node.getLeft().getKey())
        .returns(false, TreapNode::hasRight)
        .returns(false, TreapNode::hasParent);
    Assertions.assertThat(tree.getLeft())
        .returns(keyThree, TreapNode::getKey)
        .returns(priorityThree, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(keyFour, node -> node.getRight().getKey())
        .returns(keyOne, node -> node.getParent().getKey());
    Assertions.assertThat(tree.getLeft().getRight())
        .returns(keyFour, TreapNode::getKey)
        .returns(priorityFour, TreapNode::getPriority)
        .returns(false, TreapNode::hasLeft)
        .returns(false, TreapNode::hasRight)
        .returns(keyThree, node -> node.getParent().getKey());
  }

  @Test
  void shouldDeleteCaseNonExistentKey() {
    // Given
    final var key = "K";
    final var sut = masterMinTreap();

    // When
    final ThrowingCallable result = () -> sut.delete(key);

    // Then
    Assertions.assertThatThrownBy(result)
        .isInstanceOf(NoSuchElementException.class);
  }

  private Treap<String, Integer> masterMinTreap() {
    return masterTreap(Comparator.naturalOrder());
  }

  private Treap<String, Integer> masterMaxTreap() {
    return masterTreap(Comparator.reverseOrder());
  }

  private Treap<String, Integer> masterTreap(Comparator<Integer> priorityComparator) {
    var sut = new Treap<String, Integer>(priorityComparator);
    sut.put("K5", 1);
    sut.put("K3", 2);
    sut.put("K7", 3);
    sut.put("K2", 4);
    sut.put("K4", 5);
    sut.put("K6", 6);
    sut.put("K8", 7);
    sut.put("K1", 8);
    sut.put("K9", 9);
    return sut;
  }
}