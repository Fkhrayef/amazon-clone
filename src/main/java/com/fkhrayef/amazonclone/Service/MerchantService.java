package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Merchant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
public class MerchantService {
    ArrayList<Merchant> merchants = new ArrayList<>();

    public ArrayList<Merchant> getMerchants() {
        return merchants;
    }

    public Boolean addMerchant(Merchant merchant) {
        for (Merchant c : merchants) {
            if (c.getId().equals(merchant.getId())) {
                return false; // id already in use
            }
        }
        merchant.setRating(0.0);
        merchants.add(merchant);
        return true; // added successfully
    }

    public Integer updateMerchant(String id, Merchant merchant) {
        // check if new id is available
        for (Merchant c : merchants) {
            if (c.getId().equals(merchant.getId()) && !c.getId().equals(id)) {
                return 2; // new id is not available
            }
        }

        // Look for the merchant and update it if found
        for (int i = 0; i < merchants.size() ; i++) {
            if (merchants.get(i).getId().equals(id)) {
                merchants.set(i, merchant);
                return 1; // updated successfully
            }
        }
        // if not found, return false
        return 3; // merchant not found
    }

    public Boolean deleteMerchant(String id) {
        // Look for the merchant and delete it if found
        for (int i = 0; i < merchants.size() ; i++) {
            if (merchants.get(i).getId().equals(id)) {
                merchants.remove(i);
                return true;
            }
        }
        // if not found, return false
        return false;
    }

    public ArrayList<Merchant> getTopMerchants() {
        ArrayList<Merchant> topMerchants = merchants;
        topMerchants.sort(Comparator.comparingDouble(Merchant::getRating).reversed());

        int count = Math.min(5, topMerchants.size()); // setting maximum suggested product size to 5.

        return new ArrayList<>(topMerchants.subList(0, count));
    }
}
