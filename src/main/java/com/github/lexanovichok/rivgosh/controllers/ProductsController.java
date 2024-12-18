package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.Product;
import com.github.lexanovichok.rivgosh.model.User;
import com.github.lexanovichok.rivgosh.services.ProductService;
import com.github.lexanovichok.rivgosh.services.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;
    @Autowired
    private ProductsRepository repo;
    @Autowired

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"", "/"})
    public String getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search_q,
            Model model, Authentication authentication) {

//        String username = null;
//        boolean isAdmin = false;
//
//        if (authentication != null) {
//            username = authentication.getName();
//            User userOpt = userRepo.findByUsername(username);
//            isAdmin = userOpt.isPresent() && "admin".equalsIgnoreCase(userOpt.get().getRole());
//        }


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
        //model.addAttribute("isAdmin", isAdmin);
        return "products/productsBySearch"; // Thymeleaf-шаблон
    }


    @GetMapping("/search_suggestions")
    @ResponseBody
    public List<Product> searchSuggestions(@RequestParam String query) {
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

    @GetMapping("/productEdit/{id}")
    public String getProductEdit(@PathVariable Integer id, Model model) {
        Product product = repo.findById(id)
                .orElse(null);


        if (product == null) {
            model.addAttribute("errorMessage", "Product not found with id: " + id);
            return "error";
        }

        model.addAttribute("product", product);
        return "products/productEdit";
    }

    @PostMapping("/productEdit/{id}")
    public String editProduct(@PathVariable Integer id, @ModelAttribute("product") Product updatedProduct) {
        Product existingProduct = repo.findById(id)
                .orElse(null);

        if (existingProduct == null) {
            return "redirect:/products?error=notfound";
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setShortDescription(updatedProduct.getShortDescription());
        existingProduct.setLongDescription(updatedProduct.getLongDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImage(updatedProduct.getImage());

        repo.save(existingProduct);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        repo.deleteById(id);
        return "redirect:/products";
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
