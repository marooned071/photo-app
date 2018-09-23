package com.example.photostorage.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/greetings")
public class GreetingsController {

    @GetMapping(value = {"/{name}", ""})
    public String sayHello(@PathVariable("name") Optional<String> name) {
        return "Hello " + name.orElse("World! ");
    }
}
