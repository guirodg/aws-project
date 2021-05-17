package com.app.aws_project01.controller;

import com.app.aws_project01.enums.EventType;
import com.app.aws_project01.model.Product;
import com.app.aws_project01.repository.ProductRepository;
import com.app.aws_project01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private ProductRepository repository;
    private ProductPublisher productPublisher;

    @Autowired
    public ProductController(ProductRepository repository, ProductPublisher productPublisher) {
        this.repository = repository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("{id}") long id) {
        Optional<Product> optProduct = repository.findById(id);
        return optProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK)).orElseGet(()
                -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = repository.save(product);

        productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "gui");
        return new ResponseEntity<Product>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("{id}") long id) {
        Optional<Product> optProduct = repository.findById(id);
        if (optProduct.isPresent()) {

            productPublisher.publishProductEvent(product, EventType.PRODUCT_UPDATE, "gui");

            return new ResponseEntity<Product>(repository.save(product), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = repository.findById(id);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            repository.delete(product);

            productPublisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "gui");

            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = repository.findByCode(code);
        if (optProduct.isPresent()) {
            return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
