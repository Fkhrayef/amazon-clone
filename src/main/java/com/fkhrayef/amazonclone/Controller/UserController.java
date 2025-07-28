package com.fkhrayef.amazonclone.Controller;

import com.fkhrayef.amazonclone.Api.ApiResponse;
import com.fkhrayef.amazonclone.Model.Product;
import com.fkhrayef.amazonclone.Model.User;
import com.fkhrayef.amazonclone.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getUsers() {
        ArrayList<User> users = userService.getUsers();

        if (!users.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(users);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No users yet. Try adding some!"));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        // add user
        if (userService.addUser(user)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("ID is already in use."));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @Valid @RequestBody User user, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        Integer status = userService.updateUser(id, user);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("New id is already in use."));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found!"));
        }
    }

    @PostMapping("/buy-product/{userId}/{productId}/{merchantId}")
    public ResponseEntity<?> buyProduct(@PathVariable("userId") String userId, @PathVariable("productId") String productId, @PathVariable("merchantId") String merchantId, @RequestParam(required = false, name = "coupon") String coupon) {
        Integer status = userService.buyProduct(userId, productId, merchantId, coupon);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Bought product successfully."));
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found."));
        } else if (status == 3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant not found."));
        } else if (status == 4) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found."));
        } else if (status == 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Product is out of stock."));
        } else if (status == 6){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Insufficient funds."));
        } else { // status == 7
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid Coupon."));
        }
    }

    // ===== EXTRA BUSINESS LOGIC ENDPOINTS =====

    /**
     * EXTRA ENDPOINT #1: Product Refund System
     * Allows customers to return purchased products and receive full refunds.
     * Features: Balance restoration, stock replenishment, merchant rating impact, 
     *          carbon footprint reduction. (Loyalty points are preserved for customer satisfaction)
     */
    @PostMapping("/refund-product/{userId}/{productId}/{merchantId}")
    public ResponseEntity<?> refundProduct(@PathVariable("userId") String userId, @PathVariable("productId") String productId, @PathVariable("merchantId") String merchantId) {
        Integer status = userService.refundProduct(userId, productId, merchantId);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Refunded product successfully."));
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found."));
        } else if (status == 3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant not found."));
        } else if (status == 4) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found."));
        } else { // status == 5
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("MerchantStock not found."));
        }
    }

    /**
     * EXTRA ENDPOINT #2: Smart Product Recommendation System
     * Provides personalized product suggestions based on country-specific popularity,
     * product views, and environmental impact scoring with weighted algorithms.
     * Features: Multi-factor scoring, country-based preferences, eco-conscious recommendations.
     */
    @GetMapping("/get/suggested-products/{userId}")
    public ResponseEntity<?> getSuggestedProducts(@PathVariable("userId") String userId) {
        ArrayList<Product> suggestedProducts = userService.getSuggestedProducts(userId);

        if (suggestedProducts == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("User ID not found."));
        }

        if (!suggestedProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(suggestedProducts);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No suggested products."));
        }
    }

    /**
     * EXTRA ENDPOINT #3: Admin Discount Management System
     * Enables administrators to create and assign discount coupons to specific merchant stock.
     * Features: Role-based access control, coupon format validation, percentage-based discounts.
     * Coupon Format: [4 letters]-[1-2 digits] (e.g., SAVE-25 for 25% off)
     */
    @PostMapping("/add-discount/{userId}/{merchantStockId}")
    public ResponseEntity<?> addDiscount(@PathVariable("userId") String userId, @PathVariable("merchantStockId") String merchantStockId, @RequestParam("coupon") String coupon) {
        // validate coupon format
        if (!coupon.matches("^[a-zA-Z]{4}-\\d{1,2}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Coupon is invalid."));
        }

        // add coupon
        int status = userService.addDiscount(userId, merchantStockId, coupon);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Coupon added successfully."));
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("User is not Admin."));
        } else if (status == 3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found."));
        } else { // status == 4
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("MerchantStock not found."));
        }
    }

    /**
     * EXTRA ENDPOINT #4: Gift Card Purchase System
     * Allows users to purchase digital gift cards in predefined denominations.
     * Features: Fixed amount validation, balance deduction, unique code generation,
     *          automatic gift card product creation with timestamp-based IDs.
     * Available amounts: $10, $20, $50, $80, $100
     */
    @PostMapping("/buy-gift-card/{userId}/{amount}")
    public ResponseEntity<?> buyGiftCard(@PathVariable("userId") String userId, @PathVariable("amount") Double amount) {
        String response = userService.buyGiftCard(userId, amount);

        if (response.startsWith("GC")) {
            // Success - response is the gift card code
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Gift card purchased successfully. Code: " + response));
        } else if (response.equals("2")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid amount. Choose: 10, 20, 50, 80, or 100"));
        } else if (response.equals("3")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found"));
        } else { // "4"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Insufficient balance"));
        }
    }

    /**
     * EXTRA ENDPOINT #5: Gift Card Redemption System
     * Enables users to redeem gift card codes and add value to their account balance.
     * Features: Gift card validation, balance addition, automatic product cleanup,
     *          one-time use enforcement to prevent fraud.
     */
    @PostMapping("/redeem-gift-card/{userId}/{giftCardCode}")
    public ResponseEntity<?> redeemGiftCard(@PathVariable String userId, @PathVariable String giftCardCode) {
        Integer status = userService.redeemGiftCard(userId, giftCardCode);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Gift card redeemed successfully!"));
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found"));
        } else { // status == 3
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid or already used gift card"));
        }
    }

    /**
     * EXTRA ENDPOINT #6: Loyalty Points Redemption System
     * Allows customers to convert earned loyalty points into account balance.
     * Features: Points validation (minimum 100, multiples of 100), 1% reward rate,
     *          balance conversion (100 points = $1), point deduction tracking.
     * Earning: 1 point per $1 spent | Redemption: 100 points = $1 balance
     */
    @PostMapping("/spend-points/{userId}/{points}")
    public ResponseEntity<?> spendPoints(@PathVariable String userId, @PathVariable Integer points) {
        Integer status = userService.spendPoints(userId, points);

        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Points redeemed successfully!"));
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid points amount. Must be multiple of 100 (minimum 100)"));
        } else if (status == 3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found"));
        } else { // status == 4
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Insufficient loyalty points"));
        }
    }

    /**
     * EXTRA ENDPOINT #7: Personal Carbon Footprint Tracker
     * Provides users with their total environmental impact from all purchases.
     * Features: Real-time carbon tracking, cumulative footprint calculation,
     *          environmental awareness promotion, refund impact consideration.
     * Measurement: kg CO2 equivalent from all purchased products
     */
    @GetMapping("/get/carbon-footprint/{userId}")
    public ResponseEntity<?> getUserCarbonFootprint(@PathVariable String userId) {
        Double carbonFootprint = userService.getUserCarbonFootprint(userId);

        if (carbonFootprint != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Carbon footprint: " + carbonFootprint + " kg CO2"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("User not found"));
        }
    }

    /**
     * EXTRA ENDPOINT #8: Carbon Footprint Leaderboard
     * Displays top 3 most eco-conscious users with lowest carbon footprints.
     * Features: Environmental ranking system, sustainability gamification,
     *          community awareness building, eco-friendly behavior incentivization.
     * Ranking: Users sorted by lowest total carbon footprint (most eco-friendly first)
     */
    @GetMapping("/get/carbon-leaderboard")
    public ResponseEntity<?> getCarbonFootprintLeaderboard() {
        ArrayList<User> leaderboard = userService.getCarbonFootprintLeaderboard();

        if (!leaderboard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(leaderboard);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("No users found"));
        }
    }
}
