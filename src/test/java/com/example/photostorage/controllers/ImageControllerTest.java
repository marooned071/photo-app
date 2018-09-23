package com.example.photostorage.controllers;

import com.example.photostorage.PhotoStorageApplication;
import com.example.photostorage.config.MongoConfig;
import com.example.photostorage.entity.ImageInfo;
import com.google.common.primitives.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PhotoStorageApplication.class, MongoConfig.class})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ImageControllerTest {
    private final File file1 = Paths.get("src/test/resources", "wroclaw1.jpg").toFile();
    private final File file2 = Paths.get("src/test/resources", "wroclaw2.jpg").toFile();
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;
    private ImageInfo imageInfo1 = new ImageInfo("Wroclaw za dnia", "Widok na Wroclaw w dzien");
    private ImageInfo imageInfo2 = new ImageInfo("Wroclaw wieczorem", "Wieczor nad Wroclawiem");

    @Before
    public void setUp() {
        reactiveMongoOperations.dropCollection(ImageInfo.class).then().block();
    }

    @Test
    public void shouldReturnAllImageInfo() {
        ImageInfo returnedImageInfo1 = postImage(imageInfo1, file1);
        ImageInfo returnedImageInfo2 = postImage(imageInfo2, file2);

        webTestClient.get().uri("files/").exchange().expectBodyList(ImageInfo.class)
                .contains(returnedImageInfo1, returnedImageInfo2);
    }

    @Test
    public void whenNotFoundShouldReturn404() {
        postImage(imageInfo1, file1);
        postImage(imageInfo2, file2);

        webTestClient.get().uri("files/" + "uknonwnID").exchange().expectStatus().isNotFound();
    }


    @Test
    public void shouldReturnImages() throws IOException {
        ImageInfo returnedImageInfo1 = postImage(imageInfo1, file1);
        ImageInfo returnedImageInfo2 = postImage(imageInfo2, file2);

        byte[] responseBodyImage1 = webTestClient.get().uri("files/" + returnedImageInfo1.getFileId()).exchange().expectBody().returnResult().getResponseBody();
        byte[] responseBodyImage2 = webTestClient.get().uri("files/" + returnedImageInfo2.getFileId()).exchange().expectBody().returnResult().getResponseBody();

        assertEquals(Bytes.asList(responseBodyImage1), Bytes.asList(new PathResource(file1.getPath()).getInputStream().readAllBytes()));
        assertEquals(Bytes.asList(responseBodyImage2), Bytes.asList(new PathResource(file2.getPath()).getInputStream().readAllBytes()));
    }

    @Test
    public void shouldDeleteImage() {
        ImageInfo returnedImageInfo1 = postImage(imageInfo1, file1);
        ImageInfo returnedImageInfo2 = postImage(imageInfo2, file2);

        webTestClient.delete().uri("files/" + returnedImageInfo1.getId()).exchange();

        webTestClient.get().uri("files/").exchange().
                expectBodyList(ImageInfo.class).contains(returnedImageInfo2).doesNotContain(returnedImageInfo1);
    }

    @Test
    public void shouldDeleteAll() {
        postImage(imageInfo1, file1);
        postImage(imageInfo2, file2);

        webTestClient.delete().uri("files/").exchange();

        webTestClient.get().uri("files/").exchange().expectBodyList(ImageInfo.class).hasSize(0);
    }

    private ImageInfo postImage(ImageInfo imageInfo, File file) {
        ImageInfo returnedImageInfo = webTestClient.post().uri("files/upload").contentType(MediaType.MULTIPART_FORM_DATA)
                .syncBody(generateBody(imageInfo, file)).exchange().returnResult(ImageInfo.class)
                .getResponseBody().blockFirst();
        assertEquals(imageInfo.getDescription(), returnedImageInfo.getDescription());
        return returnedImageInfo;
    }


    private MultiValueMap<String, HttpEntity<?>> generateBody(ImageInfo imageInfo, File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", imageInfo.getTitle());
        builder.part("description", imageInfo.getDescription());
        builder.part("file", new PathResource(file.getPath()));
        return builder.build();
    }
}