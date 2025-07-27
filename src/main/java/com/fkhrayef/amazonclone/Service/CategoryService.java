package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {

    ArrayList<Category> categories = new ArrayList<>();

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public Boolean updateCategory(String id, Category category) {
        // Look for the category and update it if found
        for (int i = 0; i < categories.size() ; i++) {
            if (categories.get(i).getId().equals(id)) {
                categories.set(i, category);
                return true;
            }
        }
        // if not found, return false
        return false;
    }
}
