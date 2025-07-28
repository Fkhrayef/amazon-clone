package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Category;
import com.fkhrayef.amazonclone.Model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryService categoryService;

    ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Integer addProduct(Product product) {
        // check if id is available
        for (Product p : products) {
            if (p.getId().equals(product.getId())) {
                return 2; // id is not available
            }
        }

        for (Category c : categoryService.getCategories()) {
            if (c.getId().equals(product.getCategoryId())) {
                product.setSaudiBuyCount(0);
                product.setKuwaitBuyCount(0);
                products.add(product);
                return 1; // added successfully
            }
        }
        return 3; // category not found
    }

    public Integer updateProduct(String id, Product product) {
        // check if new id is available
        for (Product p : products) {
            if (p.getId().equals(product.getId()) && !p.getId().equals(id)) {
                return 3; // new id is not available
            }
        }

        // Look for the product and update it if found
        for (int i = 0; i < products.size() ; i++) {
            if (products.get(i).getId().equals(id)) {
                for (Category c : categoryService.getCategories()) {
                    if (c.getId().equals(product.getCategoryId())) {
                        products.set(i, product);
                        return 1; // updated successfully
                    }
                }
                return 2; // Product found but category is not found
            }
        }
        // if not found, return false
        return 4; // Product not found
    }

    public Boolean deleteProduct(String id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                products.remove(i);
                return true;
            }
        }
        return false;
    }
}
