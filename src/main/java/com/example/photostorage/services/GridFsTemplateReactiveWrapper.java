package com.example.photostorage.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.Objects;

@Service
public class GridFsTemplateReactiveWrapper {
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFsTemplateReactiveWrapper(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }


    public Mono<Void> remove(String fileId) {
        Mono<Void> blockingWrapper = Mono.fromRunnable(() -> gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId))));
        return blockingWrapper.subscribeOn(Schedulers.elastic());
    }

    public Mono<ObjectId> store(Flux<DataBuffer> fileDataBuffer, String filename) {
        Mono<ObjectId> blockingStoreWrapper = Mono.fromCallable(() -> {
            InputStream inputStream = joinInputStreams(fileDataBuffer);
            return this.gridFsTemplate.store(inputStream, filename);
        });

        return blockingStoreWrapper.subscribeOn(Schedulers.elastic());
    }

    public Mono<GridFsResource> getFile(String fileId) {
        Mono<GridFsResource> blockingGetFileWrapper = Mono.fromCallable(() -> {
            GridFSFile gridFSFile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
            if (Objects.isNull(gridFSFile)) {
                return null;
            }
            return this.gridFsTemplate.getResource(gridFSFile.getFilename());
        });
        return blockingGetFileWrapper.subscribeOn(Schedulers.elastic());
    }

    public Mono<Void> removeAll() {
        Mono<Void> blockingWrapper = Mono.fromRunnable(() -> {
            this.gridFsTemplate.delete(new Query(Criteria.where("_id").exists(true)));
        });
        return blockingWrapper.subscribeOn(Schedulers.elastic());
    }

    private InputStream joinInputStreams(Flux<DataBuffer> dataBufferFlux) {
        return new DefaultDataBufferFactory().join(dataBufferFlux.collectList().block()).asInputStream();
    }
}
