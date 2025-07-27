package com.fkhrayef.amazonclone.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Category {
    @NotEmpty(message = "Category id cannot be null")
    private String id;
    @NotEmpty(message = "Category name cannot be null")
    @Size(min = 4, message = "Category name must be more than 3 character")
    private String name;

}
