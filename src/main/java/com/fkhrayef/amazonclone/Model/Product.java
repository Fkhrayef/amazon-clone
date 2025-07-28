package com.fkhrayef.amazonclone.Model;

import jakarta.validation.constraints.*;
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
    //extra
    @PositiveOrZero(message = "Saudi buy count cannot be negative")
    private Integer saudiBuyCount;
    @PositiveOrZero(message = "Kuwait buy count cannot be negative")
    private Integer kuwaitBuyCount;
    @PositiveOrZero(message = "View count cannot be negative")
    private Integer viewCount;
    @NotNull(message = "Product carbon footprint cannot be null")
    @PositiveOrZero(message = "Carbon footprint cannot be negative")
    private Double carbonFootprint; // kg CO2 per product
}
