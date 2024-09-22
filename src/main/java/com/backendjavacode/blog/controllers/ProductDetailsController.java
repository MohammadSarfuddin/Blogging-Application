package com.backendjavacode.blog.controllers;


import com.backendjavacode.blog.entities.ProductDetails;
import com.backendjavacode.blog.services.ProductDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/details")
public class ProductDetailsController {

    @Autowired
    private ProductDetailsService storeProductService;

    @PostMapping("/stock")
    public ProductDetails saveProduct(@RequestBody ProductDetails productDetails){
      return storeProductService.saveProductDetails(productDetails);
    }
}
