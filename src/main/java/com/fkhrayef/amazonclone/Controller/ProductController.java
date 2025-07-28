package com.fkhrayef.amazonclone.Controller;

import com.fkhrayef.amazonclone.Api.ApiResponse;
import com.fkhrayef.amazonclone.Model.Product;
import com.fkhrayef.amazonclone.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/get")
    public ResponseEntity<?> getProducts() {
        ArrayList<Product> products = productService.getProducts();
        if (!products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No products yet. Try adding some!"));
        }
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<?> viewProduct(@PathVariable String productId) {
        Product product = productService.getProductById(productId);

        if (product != null) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        // add product
        Integer status = productService.addProduct(product);
        if (status == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } else if(status == 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("ID is already in use."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Category not found."));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @Valid @RequestBody Product product, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        Integer status = productService.updateProduct(id, product);
        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }  else if (status == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Category not found."));
        } else if(status == 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("New id is already in use."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found."));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found."));
        }
    }
}
