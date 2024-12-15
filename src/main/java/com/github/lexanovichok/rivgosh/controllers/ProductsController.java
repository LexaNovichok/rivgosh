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
        System.out.println("Products from DB: " + products);
        model.addAttribute("products", products);
        return "products/catalog";
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

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile imageFile, Model model) throws IOException {

        // Сохранение файла
        String originalFilename = imageFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        String uploadDir = "path/to/your/upload/directory"; // Замените на ваш путь
        Path filePath = Paths.get(uploadDir, uniqueFilename);
        imageFile.transferTo(filePath);

        // Обновление модели продукта
        product.setImage(uniqueFilename);

        // Сохранение продукта в базе данных
        productService.addProduct(product);

        // Display success message or redirect to product list view
        model.addAttribute("message", "Товар успешно добавлен!");
        return "redirect:/products/catalog"; // Redirect to catalog page after successful addition
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        Product newProduct = new Product();
        model.addAttribute("product", newProduct);
        return "products/addProduct";
    }
}
