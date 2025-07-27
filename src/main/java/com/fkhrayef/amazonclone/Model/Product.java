package com.fkhrayef.amazonclone.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    @NotEmpty(message = "Product id cannot be null")
    private String id;
    @NotEmpty(message = "Product name cannot be null")
    @Size(min = 4, message = "Product name must be more than 3 character")
    private String name;
    @NotNull(message = "Product price cannot be null")
    @Positive(message = "Product price must be positive")
    private Double price;
    @NotEmpty(message = "Product must have a category id")
    private String categoryId;
}
