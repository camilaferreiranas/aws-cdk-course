package br.com.camilaferreiranas.aws_project01.controller;

import br.com.camilaferreiranas.aws_project01.enums.EventType;
import br.com.camilaferreiranas.aws_project01.model.Product;
import br.com.camilaferreiranas.aws_project01.repository.ProductRepository;
import br.com.camilaferreiranas.aws_project01.service.ProducPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/products")
@RestController

public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    ProducPublisher producPublisher;


    @GetMapping
    public Iterable<Product> getAllProducts() {
       return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        if(optionalProduct.isPresent()) {
            return new ResponseEntity<Product>(optionalProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product  product1 = repository.save(product);
        producPublisher.publishProductEvent(product1, EventType.PRODUCT_CREATED, "camila_create");
        return new ResponseEntity<Product>(product1, HttpStatus.CREATED);
    }


    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
        if(repository.existsById(id)) {
            product.setId(id);
            Product product1 = repository.save(product);
            producPublisher.publishProductEvent(product1, EventType.PRODUCT_UPDATE, "camila_update");
            return new ResponseEntity<Product>(product1, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        if(optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            repository.delete(product);
            producPublisher.publishProductEvent(product, EventType.PRODUCT_DELETE, "camila_delete");
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optionalProduct = repository.findByCode(code);
        if(optionalProduct.isPresent()) {
            return new ResponseEntity<Product>(optionalProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
