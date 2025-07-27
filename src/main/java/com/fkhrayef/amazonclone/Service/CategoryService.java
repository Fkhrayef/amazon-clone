package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Category;
import com.fkhrayef.amazonclone.Model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {

    ArrayList<Category> categories = new ArrayList<>();

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public Boolean addCategory(Category category) {
        for (Category c : categories) {
            if (c.getId().equals(category.getId())) {
                return false;
            }
        }
        categories.add(category);
        return true;
    }

    public Integer updateCategory(String id, Category category) {
        // check if new id is available
        for (Category c : categories) {
            if (c.getId().equals(category.getId()) && !c.getId().equals(id)) {
                return 2; // new id is not available
            }
        }

        // Look for the category and update it if found
        for (int i = 0; i < categories.size() ; i++) {
            if (categories.get(i).getId().equals(id)) {
                categories.set(i, category);
                return 1; // updated successfully
            }
        }
        // if not found, return false
        return 3; // category not found
    }

    public Boolean deleteCategory(String id) {
        // Look for the category and update it if found
        for (int i = 0; i < categories.size() ; i++) {
            if (categories.get(i).getId().equals(id)) {
                categories.remove(i);
                return true;
            }
        }
        // if not found, return false
        return false;
    }
}
