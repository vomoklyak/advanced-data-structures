package com.lesson.ads.chapter12;

import com.lesson.ads.util.Point;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClusterViewer extends JPanel {

  private static ClusterView CLUSTER_VIEW;

  public static void view(ClusterView clusterView) {
    CLUSTER_VIEW = clusterView;
    ClusterViewer.main(new String[]{});
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      var jFrame = new JFrame();
      jFrame.setTitle("Cluster View");
      jFrame.setVisible(true);
      jFrame.setResizable(false);
      jFrame.setLocationRelativeTo(null);
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jFrame.add(new ClusterViewer(700, 700), BorderLayout.CENTER);
      jFrame.pack();
    });
  }

  public ClusterViewer(int width, int height) {
    setPreferredSize(new Dimension(width, height));
    setBackground(Color.white);
  }

  @Override
  public void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    var graphics2D = (Graphics2D) graphics;
    graphics2D.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    draw(graphics2D);
  }

  private void draw(Graphics2D graphics2D) {
    var colors = List.of(
        Color.cyan,
        Color.green,
        Color.gray,
        Color.orange,
        Color.blue,
        Color.red,
        Color.yellow
    );
    var centerAxesX = getWidth() / 2;
    var centerAxesY = getHeight() / 2;

    IntFunction<Color> pointIndexToColor = pointIndex -> {
      var pointClusterIndex = CLUSTER_VIEW.pointClusterIndexes()[pointIndex];
      return pointClusterIndex == CLUSTER_VIEW.outlierIndex()
          ? Color.red : colors.get(pointClusterIndex);
    };
    graphics2D.setStroke(new BasicStroke(2));
    IntStream.range(0, CLUSTER_VIEW.points().length).forEach(index -> {
      var clusterColor = pointIndexToColor.apply(index);
      graphics2D.setColor(clusterColor);
      plot(
          graphics2D,
          (int) (centerAxesX + CLUSTER_VIEW.points()[index].coordinate(0)),
          (int) (centerAxesY - CLUSTER_VIEW.points()[index].coordinate(1))
      );
    });

    graphics2D.setStroke(new BasicStroke(8));
    IntStream.range(0, CLUSTER_VIEW.clusterCentroids().length).forEach(index -> {
      graphics2D.setColor(colors.get(index));
      plot(
          graphics2D,
          (int) (centerAxesX + CLUSTER_VIEW.clusterCentroids()[index].coordinate(0)),
          (int) (centerAxesY - CLUSTER_VIEW.clusterCentroids()[index].coordinate(1))
      );
    });
  }

  private void plot(Graphics2D g, int axesX, int axesY) {
    g.drawOval(axesX, axesY, 1, 1);
  }

  public interface ClusterView {

    Point[] points();

    int[] pointClusterIndexes();

    Point[] clusterCentroids();

    int outlierIndex();
  }
}