package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Merchant;
import com.fkhrayef.amazonclone.Model.MerchantStock;
import com.fkhrayef.amazonclone.Model.Product;
import com.fkhrayef.amazonclone.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProductService productService;
    private final MerchantService merchantService;
    private final MerchantStockService merchantStockService;

    ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public Boolean addUser(User user) {
        // check if id is available
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                return false; // id is not available
            }
        }
        
        users.add(user);
        return true; // added successfully
    }

    public Integer updateUser(String id, User user) {
        
        // Look for the user and update it if found
        for (int i = 0; i < users.size() ; i++) {
            if (users.get(i).getId().equals(id)) {
                // check if new id is available
                for (User u : users) {
                    if (u.getId().equals(user.getId()) && !u.getId().equals(id)) {
                        return 3; // new id is not available
                    }
                }
                users.set(i, user);
                return 1; // updated successfully
            }
        }
        // if not found, return false
        return 2; // user not found
    }

    public Boolean deleteUser(String id) {
        // Look for the user and delete it if found
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.remove(i);
                return true;
            }
        }
        // if not found, return false
        return false;
    }

    public Integer buyProduct(String userId, String productId, String merchantId) {

        // Check if user exists
        boolean userExists = false;
        User user = null; // to use later (saves us a for loop of O(n))
        for (User u : users) {
            if (u.getId().equals(userId)) {
                userExists = true;
                user = u;
                break;
            }
        }
        if (!userExists) return 2; // User not found

        // Check if merchant exists + is in stock
        boolean merchantExists = false;
        for (Merchant m : merchantService.getMerchants()) {
            if (m.getId().equals(merchantId)) {
                merchantExists = true;
                break;
            }
        }
        if (!merchantExists) return 3; // Merchant not found

        // Check if product exists
        boolean productExists = false;
        Product product = null; // to use later (saves us a for loop of O(n))
        for (Product p : productService.getProducts()) {
            if (p.getId().equals(productId)) {
                productExists = true;
                product = p;
                break;
            }
        }
        if (!productExists) return 4; // Product not found

        // Check if product in stock
        boolean productInStock = false;
        MerchantStock merchantStock = null; // to use later (saves us a for loop of O(n))
        for (MerchantStock m : merchantStockService.getMerchantStocks()) {
            if (m.getProductId().equals(productId) && m.getMerchantId().equals(merchantId)) {
                if (m.getStock() >= 1) {
                    productInStock = true;
                    merchantStock = m;
                    break;
                }
            }
        }
        if (!productInStock) return 5; // product is out of stock

        // Check if user has sufficient funds
        if (user.getBalance() < product.getPrice()) {
            return 6; // insufficient funds
        }

        // success
        merchantStock.setStock(merchantStock.getStock() - 1); // reduce stock
        user.setBalance(user.getBalance() - product.getPrice()); // reduce balance
        if (user.getCountry().equals("Saudi Arabia")) {
            product.setSaudiBuyCount(product.getSaudiBuyCount() + 1); // saudi bought the product
        } else {
            product.setKuwaitBuyCount(product.getKuwaitBuyCount() + 1); // kuwaiti bought the product
        }
        return 1; // Bought the product successfully
    }

    public ArrayList<Product> getSuggestedProducts(String userId) {
        ArrayList<Product> products = productService.getProducts();

        // get user
        User user = null;
        boolean userExists = false;
        for (User u : users) {
            if (u.getId().equals(userId)) {
                userExists = true;
                user = u;
                break;
            }
        }

        if (!userExists) return null; // user doesn't exist

        if (user.getCountry().equals("Saudi Arabia")) {
            products.sort(Comparator.comparingInt(Product::getSaudiBuyCount).reversed());
        } else {
            products.sort(Comparator.comparingInt(Product::getKuwaitBuyCount).reversed());
        }

        int count = Math.min(15, products.size()); // setting maximum suggested product size to 15.

        ArrayList<Product> suggestedProducts = new ArrayList<>(products.subList(0, count));

        return suggestedProducts;
    }
}
