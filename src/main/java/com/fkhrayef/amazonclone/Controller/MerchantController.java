package com.fkhrayef.amazonclone.Controller;

import com.fkhrayef.amazonclone.Api.ApiResponse;
import com.fkhrayef.amazonclone.Model.Merchant;
import com.fkhrayef.amazonclone.Service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/get")
    public ResponseEntity<?> getMerchants() {
        ArrayList<Merchant> merchants = merchantService.getMerchants();

        if (!merchants.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(merchants);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No merchants yet. Try adding some!"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMerchant(@Valid @RequestBody Merchant merchant, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        // add merchant
        if (merchantService.addMerchant(merchant)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(merchant);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("ID is already in use."));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable("id") String id, @Valid @RequestBody Merchant merchant, Errors errors) {
        // Check for validation errors
        if (errors.hasErrors()) {
            ArrayList<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors.getFieldErrors())
                errorMessages.add(error.getDefaultMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        Integer status = merchantService.updateMerchant(id, merchant);
        if (status == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(merchant);
        } else if (status == 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("New id is already in use."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant not found."));
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable("id") String id) {
        if (merchantService.deleteMerchant(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant not found."));
        }
    }
}
