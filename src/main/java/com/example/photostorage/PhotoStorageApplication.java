package com.example.photostorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;


@SpringBootApplication
public class PhotoStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoStorageApplication.class, args);
    }
}
