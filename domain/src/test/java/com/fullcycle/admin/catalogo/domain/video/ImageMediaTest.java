package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageMediaTest extends UnitTest {

    @Test
    public void givenValidParams_whenCallsNewImage_shouldReturnInstance() {

        final var expectedChecksum = "abc";
        final var expectedName = "Banner.png";
        final var expectedLocation = "/images/ac";

        final var actualImage = ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

        Assertions.assertNotNull(actualImage);
        Assertions.assertEquals(expectedChecksum, actualImage.checkSum());
        Assertions.assertEquals(expectedName, actualImage.name());
        Assertions.assertEquals(expectedLocation, actualImage.location());
    }

    @Test
    public void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_shouldReturnTrue() {

        final var expectedChecksum = "abc";
        final var expectedLocation = "/images/ac";

        final var actualImage1 = ImageMedia.with(expectedChecksum, "Random", expectedLocation);
        final var actualImage2 = ImageMedia.with(expectedChecksum, "Simple", expectedLocation);

        Assertions.assertEquals(actualImage1, actualImage2);
        Assertions.assertNotSame(actualImage1, actualImage2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_shouldReturnError() {
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with(null, "Random", "/images"));
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with("abc", null, "/images"));
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with("abc", "Random", null));
    }
}
