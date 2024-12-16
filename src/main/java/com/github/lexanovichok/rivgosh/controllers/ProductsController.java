package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.Product;
import com.github.lexanovichok.rivgosh.services.ProductService;
import com.github.lexanovichok.rivgosh.services.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
    public String getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search_q,
            Model model) {

        List<Product> filteredProducts = repo.findAll();

        if (category != null && !category.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        if (search_q != null && !search_q.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getName().toLowerCase().contains(search_q.toLowerCase()))
                    .toList();
        }

        model.addAttribute("products", filteredProducts);
        model.addAttribute("selectedCategory", category);
        return "products/productsBySearch"; // Thymeleaf-шаблон
    }


    @GetMapping("/search_suggestions")
    @ResponseBody
    public List<Product> searchSuggestions(@RequestParam String query) {
        // Возвращаем список товаров, где название содержит введенную строку
        return repo.findByNameContainingIgnoreCase(query);
    }



    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable Integer id, Model model) {
        Product product = repo.findById(id)
                .orElse(null);


        if (product == null) {
            model.addAttribute("errorMessage", "Product not found with id: " + id);
            return "error";
        }

        model.addAttribute("product", product);
        return "products/product";
    }


    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/addProduct"; // Thymeleaf-шаблон
    }

    // Обработка формы добавления товара
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }


}
