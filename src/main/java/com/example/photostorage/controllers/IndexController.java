package com.example.photostorage.controllers;

import com.example.photostorage.entity.FileForm;
import com.example.photostorage.entity.ImageInfo;
import com.example.photostorage.services.ImageStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class IndexController {

    private final ImageStorageService imageStorageService;

    public IndexController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Flux<ImageInfo> all = imageStorageService.getAll();
        model.addAttribute("photos", all);
        model.addAttribute("FileForm", new FileForm());
        return "index";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Rendering saveFile(@ModelAttribute FileForm fileForm) {
        FilePart file = fileForm.getFile();
        Mono<ImageInfo> store = imageStorageService.store(file.content(), file.filename(), fileForm.getTitle(), fileForm.getDescription());
        store.subscribe();
        return Rendering.redirectTo("/").build();
    }


    @GetMapping("/upload")
    public String fileForm(Model model) {
        model.addAttribute("FileForm", new FileForm());
        return "index";
    }

    @DeleteMapping("/remove/{id}")
    public Rendering delete(@PathVariable("id") String id){
        imageStorageService.remove(id).subscribe();
        return Rendering.redirectTo("/").build();
    }

}
