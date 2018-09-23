package com.example.photostorage.config;

import com.example.photostorage.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;
import java.util.List;

@Component
@Profile("dev")
public class StartupDevInitializer {
    private static List<String> FILE_NAMES = List.of("wro10.png", "wro2.jpg", "wro4.jpg", "wro6.jpg", "wro8.jpg",
            "wro1.jpeg", "wro3.jpg", "wro5.jpg", "wro7.jpg", "wro9.jpg");


    private final ImageStorageService imageStorageService;

    @Autowired
    public StartupDevInitializer(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDevData() {
        imageStorageService.removeAll();

        FILE_NAMES.forEach(name -> {
            PathResource wroclaw1Resource = new PathResource(Paths.get("src/main/resources/dev", name));
            Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(wroclaw1Resource, new DefaultDataBufferFactory(), 4096);
            imageStorageService.store(dataBufferFlux, name, "Title " + name, "Sample Description " + name).subscribe();
        });
    }
}