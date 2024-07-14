package com.example.demo;

import com.example.demo.products.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;

    public List<Product> findAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepo.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    public Optional<Product> changeProductPrice(Long id, Double newPrice) {
        return productRepo.updatePrice(id, newPrice);
    }

    public Optional<Product> findProductByName(String name) {
        return productRepo.findByName(name);
    }
}
