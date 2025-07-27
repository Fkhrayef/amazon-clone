package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.Merchant;
import com.fkhrayef.amazonclone.Model.Product;
import com.fkhrayef.amazonclone.Model.MerchantStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MerchantStockService {

    private final ProductService productService;
    private final MerchantService merchantService;

    ArrayList<MerchantStock> merchantStocks = new ArrayList<>();

    public ArrayList<MerchantStock> getMerchantStocks() {
        return merchantStocks;
    }

    public Integer addMerchantStock(MerchantStock merchantStock) {
        // validate stock is not 10 or under
        if (merchantStock.getStock() <= 10) {
            return 5; // stock is 10 or under
        }

        // check if id is available
        for (MerchantStock p : merchantStocks) {
            if (p.getId().equals(merchantStock.getId())) {
                return 2; // id is not available
            }
        }

        for (Merchant m : merchantService.getMerchants()) {
            if (m.getId().equals(merchantStock.getMerchantId())) {
                for (Product c : productService.getProducts()) {
                    if (c.getId().equals(merchantStock.getProductId())) {
                        merchantStocks.add(merchantStock);
                        return 1; // added successfully
                    }
                }
                return 3; // product not found
            }
        }
        return 4; // merchant not found
    }

    public Integer updateMerchantStock(String id, MerchantStock merchantStock) {

        // Look for the merchantStock and update it if found
        for (int i = 0; i < merchantStocks.size() ; i++) {
            if (merchantStocks.get(i).getId().equals(id)) {
                for (Merchant m : merchantService.getMerchants()) {
                    if (m.getId().equals(merchantStock.getMerchantId())) {
                        for (Product c : productService.getProducts()) {
                            if (c.getId().equals(merchantStock.getProductId())) {
                                // check if new id is available
                                for (MerchantStock merchantStock1 : merchantStocks) {
                                    if (merchantStock1.getId().equals(merchantStock.getId()) && !merchantStock1.getId().equals(id)) {
                                        return 3; // new id is not available
                                    }
                                }
                                merchantStocks.set(i, merchantStock);
                                return 1; // updated successfully
                            }
                        }
                        return 2; // MerchantStock and Merchant found but Product is not found
                    }
                }
                return 5; // MerchantStock found but Merchant is not found
            }
        }
        // if not found, return false
        return 4; // MerchantStock not found
    }

    public Boolean deleteMerchantStock(String id) {
        for (int i = 0; i < merchantStocks.size(); i++) {
            if (merchantStocks.get(i).getId().equals(id)) {
                merchantStocks.remove(i);
                return true;
            }
        }
        return false;
    }
}
