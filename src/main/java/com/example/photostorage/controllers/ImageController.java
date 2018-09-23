package com.example.photostorage.controllers;

import com.example.photostorage.entity.FileForm;
import com.example.photostorage.entity.ImageInfo;
import com.example.photostorage.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/files")
@RestController
public class ImageController {

    private final ImageStorageService imageStorageService;

    @Autowired
    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping(value = "/upload_with_form_model")
    public Mono<ImageInfo> saveFile(FileForm form) {
        return imageStorageService.store(form.getFile().content(), form.getFile().filename(), form.getTitle(), form.getDescription());
    }

    @PostMapping(value = "/upload")
    public Mono<ImageInfo> saveFile(@RequestPart FilePart file, @RequestPart String title, @RequestPart String description) {
        return imageStorageService.store(file.content(), file.filename(), title, description);
    }

    @GetMapping("/")
    public Flux<ImageInfo> getAll() {
        return imageStorageService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GridFsResource>> getFile(@PathVariable("id") String fileId) {
        return imageStorageService.getFile(fileId).map(gfr -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .body(gfr)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable("id") String id){
        return imageStorageService.remove(id);
    }

    @DeleteMapping("/")
    public Mono<Void> deleteAll(){
        return imageStorageService.removeAll();

    }
}
