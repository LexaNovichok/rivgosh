package com.github.lexanovichok.rivgosh;

import com.github.lexanovichok.rivgosh.model.Product;
import com.github.lexanovichok.rivgosh.services.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class RivgoshApplicationTests {

	@Autowired
	ProductsRepository repo;

	@Test
	public void testSearch() {
		List<Product> products = repo.findByNameContainingIgnoreCase("test");
		System.out.println(products);
	}

}
