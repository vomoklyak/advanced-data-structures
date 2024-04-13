package com.lesson.ads.chapter8;

import com.lesson.ads.util.Point;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

public class ImageColorReductionProcessor {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public final static class RgbColorScales {

    public static int[][] rgbGreyScale() {
      return IntStream.range(0, 256)
          .mapToObj(index -> new int[]{index, index, index})
          .toArray(int[][]::new);
    }
  }

  @SneakyThrows
  public static void process(
      String sourcePathStr, String destinationPathStr, int[]... rgbColorScale) {
    validatePathStr(sourcePathStr, destinationPathStr);
    var destinationExtension = destinationExtension(destinationPathStr);

    var rgbColorScaleTree = new KdTree(3);
    Arrays.stream(rgbColorScale)
        .map(rgbColor -> new Point(rgbColor[0], rgbColor[1], rgbColor[2]))
        .forEach(rgbColorScaleTree::putPoint);

    var bufferedImage = ImageIO.read(Path.of(sourcePathStr).toFile());

    for (int xIndex = 0; xIndex < bufferedImage.getWidth(); xIndex++) {
      for (int yIndex = 0; yIndex < bufferedImage.getHeight(); yIndex++) {
        var rgb = bufferedImage.getRGB(xIndex, yIndex);
        var intensity = intensity(rgb);
        var reducedRgb =
            rgbColorScaleTree.getNearestPoint(new Point(red(rgb), green(rgb), blue(rgb)))
                .map(point -> rgb(
                    intensity,
                    (int) point.coordinate(0),
                    (int) point.coordinate(1),
                    (int) point.coordinate(2)
                ))
                .orElseThrow();
        bufferedImage.setRGB(xIndex, yIndex, reducedRgb);
      }
    }

    ImageIO.write(bufferedImage, destinationExtension, Path.of(destinationPathStr).toFile());
  }

  private static void validatePathStr(String sourcePathStr, String destinationPathStr) {
    if (sourcePathStr == null || sourcePathStr.isEmpty()
        || destinationPathStr == null || destinationPathStr.isEmpty()) {
      throw new IllegalArgumentException(String.format(
          "Source and destination paths cannot be null: sourcePathStr=%s, destinationPathStr=%s",
          sourcePathStr, destinationPathStr));
    }
  }

  private static String destinationExtension(String destinationPathStr) {
    return Optional.ofNullable(destinationPathStr)
        .map(path -> path.split("\\."))
        .filter(pathParts -> pathParts.length >= 2)
        .map(pathParts -> pathParts[pathParts.length - 1])
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "Cannot get destination path extension: destinationPathStr=%s", destinationPathStr)));
  }

  private static int intensity(int rgb) {
    return (rgb >> 24) & 0xFF;
  }

  private static int red(int rgb) {
    return (rgb >> 16) & 0xFF;
  }

  private static int green(int rgb) {
    return (rgb >> 8) & 0xFF;
  }

  private static int blue(int rgb) {
    return rgb & 0xFF;
  }

  private static int rgb(int intensity, int red, int green, int blue) {
    int rgb = intensity;
    rgb = (rgb << 8) + red;
    rgb = (rgb << 8) + green;
    rgb = (rgb << 8) + blue;
    return rgb;
  }
}
