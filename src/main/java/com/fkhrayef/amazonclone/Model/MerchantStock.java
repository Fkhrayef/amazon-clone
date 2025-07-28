package com.fkhrayef.amazonclone.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantStock {
    @NotEmpty(message = "MerchantStock id cannot be null")
    private String id;
    @NotEmpty(message = "MerchantStock must have a product id")
    private String productId;
    @NotEmpty(message = "MerchantStock must have a merchant id")
    private String merchantId;
    @NotNull(message = "MerchantStock stock cannot be null")
    @Positive(message = "MerchantStock stock must be positive")
    private Integer stock; // initially 10 (Logically implemented in service)
    // extra
    @Pattern(regexp = "^[a-zA-Z]{4}-\\d{1,2}$") // 4 letters then '-' then discount count
    private String coupon;
}
