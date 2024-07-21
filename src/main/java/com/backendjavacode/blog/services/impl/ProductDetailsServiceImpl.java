package com.backendjavacode.blog.services.impl;

import com.backendjavacode.blog.entities.ProductDetails;
import com.backendjavacode.blog.repositories.ProductDetailsRepository;
import com.backendjavacode.blog.services.ProductDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailsServiceImpl implements ProductDetailsService {

    @Autowired
    private ProductDetailsRepository storeProductRepository;

    @Override
    public ProductDetails saveProductDetails(ProductDetails productDetails) {

        ProductDetails details = storeProductRepository.save(productDetails);
        return details;
    }
}
