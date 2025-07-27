package com.fkhrayef.amazonclone.Controller;

import com.fkhrayef.amazonclone.Api.ApiResponse;
import com.fkhrayef.amazonclone.Model.Category;
import com.fkhrayef.amazonclone.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity<?> getCategories() {
        ArrayList<Category> categories = categoryService.getCategories();
        if (!categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(categories);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No categories yet. Try adding some!"));
        }
    }
}
