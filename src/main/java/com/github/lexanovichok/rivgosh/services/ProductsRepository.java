package com.github.lexanovichok.rivgosh.services;

import com.github.lexanovichok.rivgosh.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.longDescription LIKE %:search%")
    List<Product> searchProducts(@Param("search") String search);


    @Query("SELECT p FROM Product p WHERE p.name LIKE %:search%")
    List<Product> findByNameContainingIgnoreCase(@Param("search") String search);

    @Query("SELECT p FROM Product p")
    List<Product> getAllProducts();

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);
}
