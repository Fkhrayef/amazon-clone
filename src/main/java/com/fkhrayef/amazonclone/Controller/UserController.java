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

    // Extra: Refund a product
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

    // Extra: suggested items based on user country
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

    // Extra: allows admin to add discount to products
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

    // Extra: buy gift cards
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

    // Extra: redeem gift card
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

    // Extra: spend loyalty points
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
}
