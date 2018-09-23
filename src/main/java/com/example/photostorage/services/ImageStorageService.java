package com.example.photostorage.services;

import com.example.photostorage.entity.ImageInfo;
import com.example.photostorage.repositories.ReactiveImageInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ImageStorageService {

    private GridFsTemplateReactiveWrapper gridFsTemplateReactiveWrapper;
    private ReactiveImageInfoRepository reactiveImageInfoRepository;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    public ImageStorageService(GridFsTemplateReactiveWrapper gridFsTemplateReactiveWrapper, ReactiveImageInfoRepository reactiveImageInfoRepository) {
        this.gridFsTemplateReactiveWrapper = gridFsTemplateReactiveWrapper;
        this.reactiveImageInfoRepository = reactiveImageInfoRepository;
    }

    public Mono<ImageInfo> store(Flux<DataBuffer> fileDataBuffer, String filename, String title, String description) {
        return this.gridFsTemplateReactiveWrapper.store(fileDataBuffer, filename).
                flatMap(objectId -> this.reactiveImageInfoRepository.
                        save(new ImageInfo(objectId.toString(), filename, title, description))).
                doOnSuccess((imageInfo -> logger.info("Storing {} with id {}", imageInfo.getFileName(), imageInfo.getFileName())));
    }

    public Flux<ImageInfo> getAll() {
        return reactiveImageInfoRepository.findAll().doOnComplete(() -> logger.info("Getting all images..."));
    }


    public Mono<GridFsResource> getFile(String fileId) {
        return gridFsTemplateReactiveWrapper.getFile(fileId);
    }

    public Mono<Void> removeAll() {
        return gridFsTemplateReactiveWrapper.removeAll().then(reactiveImageInfoRepository.deleteAll()).
                doOnSuccess((v) -> logger.info("Removing all files..."));
    }

    public Mono<Void> remove(String id) {
        return reactiveImageInfoRepository.findById(id).map(imageInfo -> gridFsTemplateReactiveWrapper.remove(imageInfo.getFileId()))
                .then(reactiveImageInfoRepository.deleteById(id)).
                        doOnSuccess((v) -> logger.info("Removing file with id {}", id));
    }
}
