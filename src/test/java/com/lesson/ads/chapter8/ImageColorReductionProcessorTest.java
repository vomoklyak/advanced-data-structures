package com.lesson.ads.chapter8;

import com.lesson.ads.chapter8.ImageColorReductionProcessor.RgbColorScales;
import java.io.File;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageColorReductionProcessorTest {

  @Test
  void shouldProcess(@TempDir Path tempDir) {
    // Given
    final var sourcePathStr = "src/test/resources/the_scream.png";
    final var destinationPath = new File(tempDir.toFile(), "the_scream_grey_scale.png");
    final var rgbGreyScale = RgbColorScales.rgbGreyScale();

    // When
    ImageColorReductionProcessor.process(
        sourcePathStr, destinationPath.getAbsolutePath(), rgbGreyScale);

    // Then
    Assertions.assertThat(destinationPath).hasSameBinaryContentAs(
        Path.of("src/test/resources/the_scream_grey_scale.png").toFile());
  }
}