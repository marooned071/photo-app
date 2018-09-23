package com.example.photostorage.repositories;

import com.example.photostorage.entity.ImageInfo;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveImageInfoRepository extends ReactiveCrudRepository<ImageInfo, String> {
    Flux<ImageInfo> findByTitle(@Param("title") String title);
}
