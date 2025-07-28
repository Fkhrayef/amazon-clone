package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CategoryService categoryService;
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

    public Integer buyProduct(String userId, String productId, String merchantId, String coupon) {

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

        // Check if merchant exists
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

        // check if user has a coupon
        boolean couponStatus = false; // no coupon provided
        if (coupon != null) {
            if (!coupon.matches("^[a-zA-Z]{4}-\\d{1,2}$") || !coupon.equals(merchantStock.getCoupon())) {
                return 7; // Invalid coupon
            } else {
                couponStatus = true;
            }
        }

        if (couponStatus) {
            // get coupon discount amount
            int discount = Integer.parseInt(coupon.substring(5));

            // Check if user has sufficient funds
            double discountedPrice = product.getPrice() * (1 - discount / 100.0);
            if (user.getBalance() < discountedPrice) {
                return 6; // insufficient funds
            }
            user.setBalance(user.getBalance() - discountedPrice); // reduce balance with discount
        } else {
            // Check if user has sufficient funds
            if (user.getBalance() < product.getPrice()) {
                return 6; // insufficient funds
            }
            user.setBalance(user.getBalance() - product.getPrice()); // reduce balance without discount
        }
        // success
        merchantStock.setStock(merchantStock.getStock() - 1); // reduce stock
        if (user.getCountry().equals("Saudi Arabia")) {
            product.setSaudiBuyCount(product.getSaudiBuyCount() + 1); // saudi bought the product
        } else {
            product.setKuwaitBuyCount(product.getKuwaitBuyCount() + 1); // kuwaiti bought the product
        }
        return 1; // Bought the product successfully
    }

    // Extra: Refund a product
    public Integer refundProduct(String userId, String productId, String merchantId) {

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

        // Check if merchant exists
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

        // Get Merchant Stock
        MerchantStock merchantStock = null;
        for (MerchantStock m : merchantStockService.getMerchantStocks()) {
            if (m.getProductId().equals(productId) && m.getMerchantId().equals(merchantId)) {
                merchantStock = m;
                break;
            }
        }
        if (merchantStock == null) return 5; // MerchantStock not found

        // success
        merchantStock.setStock(merchantStock.getStock() + 1); // add stock
        user.setBalance(user.getBalance() + product.getPrice()); // refund balance
        if (user.getCountry().equals("Saudi Arabia")) {
            // saudi refunded the product
            if (product.getSaudiBuyCount() > 0) {
                product.setSaudiBuyCount(product.getSaudiBuyCount() - 1);
            }
        } else {
            // kuwaiti refunded the product
            if (product.getKuwaitBuyCount() > 0) {
                product.setKuwaitBuyCount(product.getKuwaitBuyCount() - 1);
            }
        }
        return 1; // Refunded the product successfully
    }

    // Extra: suggested items based on user country
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

    public Integer addDiscount(String userId, String merchantStockId, String coupon) {
        // check if user exists and his permissions
        int userState = 3; // user not found
        for (User u : users) {
            if (u.getId().equals(userId)) {
                if (u.getRole().equals("Admin")) {
                    userState = 1; // user found and has permissions
                    break;
                } else {
                    userState = 2; // user doesn't have permissions
                }
            }
        }
        if (userState == 3) {
            return 3; // user not found
        } else if (userState == 2) {
            return 2; // user doesn't have permissions
        } else {
            for (MerchantStock m : merchantStockService.getMerchantStocks()) {
                if (m.getId().equals(merchantStockId)) {
                    m.setCoupon(coupon);
                    return 1; // user found and has permissions
                }
            }
            return 4; // MerchantStock not found
        }
    }

    // Extra: allows users to buy gift cards
    public String buyGiftCard(String userId, Double amount) {
        if (amount != 10 && amount != 20 && amount != 50 && amount != 80 && amount != 100) {
            return "2"; // invalid gift card amount
        }

        boolean userExists = false;
        User user = null;
        for (User u : users) {
            if (u.getId().equals(userId)) {
                userExists = true;
                user = u;
                break;
            }
        }

        if (!userExists) return "3"; // user not found

        if (user.getBalance() < amount) {
            return "4"; // insufficient amount
        }

        // Deduct the gift card amount from user's balance
        user.setBalance(user.getBalance() - amount);

        // check if category is created
        boolean categoryExist = false;

        for (Category c : categoryService.getCategories()) {
            if (c.getId().equals("gift-cards")) {
                categoryExist = true;
            }
        }

        // if it's not created, create it
        if (!categoryExist) {
            // add the category
            categoryService.addCategory(new Category("gift-cards", "Gift Cards"));
        }

        // success
        String giftCardCode = "GC" + System.currentTimeMillis();
        // Create new Product (gift card)
        Product giftCard = new Product(
                giftCardCode,                    // id
                "Gift Card $" + amount,          // name
                amount,                          // price
                "gift-cards",                    // categoryId
                0,                              // saudiBuyCount
                0                               // kuwaitBuyCount
        );

        // Add to products
        productService.addProduct(giftCard);

        // Return gift card code
        return giftCardCode;
    }
}
