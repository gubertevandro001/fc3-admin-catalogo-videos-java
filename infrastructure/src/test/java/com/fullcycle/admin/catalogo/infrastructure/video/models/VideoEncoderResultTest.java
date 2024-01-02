package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JacksonTest
public class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> json;

    @Test
    public void testUnmarshallSuccessResult() throws IOException {
        final var expectedId = IdUtils.uuid();
        final var expectedOutputBucket = "codeeducationtest";
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var json = """
                {
                  "id": "%s",
                  "output_bucket_path": "%s",
                  "status": "%s",
                  "video": {
                    "encoded_video_folder": "%s",
                    "resource_id": "%s",
                    "file_path": "%s"
                  }
                }
                """.formatted(expectedId, expectedOutputBucket, expectedStatus, expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var actualResult = this.json.parse(json);

        Assertions.assertThat(actualResult).isInstanceOf(VideoEncoderCompleted.class)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("outputBucket", expectedOutputBucket)
                .hasFieldOrPropertyWithValue("video", expectedMetadata);
    }

    @Test
    public void testUnmarshallErrorResult() throws IOException {
        final var expectedMessage = "Resource not found";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedStatus = "ERROR";
        final var expectedFilePath = "any.mp4";

        final var json = """
                {
                  "status": "%s",
                  "error": "%s",
                  "message": {
                    "resource_id": "%s",
                    "file_path": "%s"
                  }
                }
                """.formatted(expectedStatus, expectedMessage, expectedResourceId, expectedFilePath);

        final var actualResult = this.json.parse(json);

        Assertions.assertThat(actualResult).isInstanceOf(VideoEncoderError.class)
                .hasFieldOrPropertyWithValue("error", expectedMessage)
                .hasFieldOrPropertyWithValue("message", new VideoMessage(expectedResourceId, expectedFilePath));
    }
}
