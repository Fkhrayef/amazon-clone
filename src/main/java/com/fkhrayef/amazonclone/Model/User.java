package com.fkhrayef.amazonclone.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    @NotEmpty(message = "User id cannot be null")
    private String id;
    @NotEmpty(message = "User username cannot be null")
    @Size(min = 6, message = "User username must be more than 5 character")
    private String username;
    @NotEmpty(message = "User password cannot be null")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,}$",
            message = "Password must be more than 6 characters long and include both letters and digits"
    )
    private String password;
    @Email(message = "User email must be in a valid format")
    private String email;
    @NotEmpty(message = "User role cannot be null")
    @Pattern(regexp = "^(Admin|Customer)$", message = "User role must be either 'Admin' or 'Customer'")
    private String role;
    @NotNull(message = "User balance cannot be null")
    @Positive(message = "User balance must be positive")
    private Double balance;
    // extra
    @NotEmpty(message = "User country cannot be null")
    @Pattern(regexp = "^(Saudi Arabia|Kuwait)$", message = "Country must be either 'Saudi Arabia' or 'Kuwait'")
    private String country;
}
