package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.Product;
import com.github.lexanovichok.rivgosh.services.ProductService;
import com.github.lexanovichok.rivgosh.services.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;
    @Autowired
    private ProductsRepository repo;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"", "/"})
    public String showProductListBySearch(
            @RequestParam(value = "search_q", required = false) String search,
            Model model) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            // Фильтруем товары по названию
            products = repo.findByNameContainingIgnoreCase(search);
        } else {
            // Возвращаем все товары, если поиска нет
            products = repo.findAll();
        }

        model.addAttribute("products", products);
        return "products/productsBySearch"; // Thymeleaf-шаблон
    }

    @GetMapping("/search_suggestions")
    @ResponseBody
    public List<Product> searchSuggestions(@RequestParam String query) {
        // Возвращаем список товаров, где название содержит введенную строку
        return repo.findByNameContainingIgnoreCase(query);
    }


    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "catalog";
    }
}
