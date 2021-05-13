package com.app.aws_project01.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/test")
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name) {
        LOG.info("Test controller = name: {}", name);

        return ResponseEntity.ok("Name: " + name);
    }

    @GetMapping("/pessoa/{name}")
    public ResponseEntity<?> pessoaTest(@RequestBody String name) {
        LOG.info("Pessoa controller = name: {}", name);

        return ResponseEntity.ok("Name: " + name);
    }
}
