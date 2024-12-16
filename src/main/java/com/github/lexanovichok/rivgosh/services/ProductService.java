package com.github.lexanovichok.rivgosh.services;

import com.github.lexanovichok.rivgosh.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<Product> getAllProducts() {
        return productsRepository.getAllProducts();
    }

    public void saveProduct(Product product) {
        productsRepository.save(product);
    }
}
