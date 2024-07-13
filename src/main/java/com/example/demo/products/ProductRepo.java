package com.example.demo.products;

import com.example.demo.Product;
import com.example.demo.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class ProductRepo {

    private final FileUtil fileUtil;
    private final Lock lock = new ReentrantLock();
    private Long nextId = 1L;

//    private ObjectMapper objectMapper = new ObjectMapper();
//    private static final String DATABASE_FILE = "database.json";

    @Autowired
    public ProductRepo(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
        try {
            List<Product> products = fileUtil.readProductsFromFile();
            if (!products.isEmpty()) {
                nextId = products.stream().mapToLong(Product::getId).max().orElse(0) + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Product> findAll() {
        lock.lock();
        try {
            return fileUtil.readProductsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        } finally {
            lock.unlock();
        }
    }

    public Optional<Product> findById(Long id) {
        lock.lock();
        try {
            return fileUtil.readProductsFromFile().stream().filter(product -> product.getId().equals(id)).findFirst();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    public Product save(Product product) {
        lock.lock();
        try {
            List<Product> products = fileUtil.readProductsFromFile();
            if (product.getId() == null) {
                product.setId(nextId++);
            }
            products.add(product);
            fileUtil.writeProductsToFile(products);
            return product;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Product> updatePrice(Long id, Double newPrice) {
        lock.lock();
        try {
            List<Product> products = fileUtil.readProductsFromFile();
            Optional<Product> productOpt = products.stream().filter(product -> product.getId().equals(id)).findFirst();
            productOpt.ifPresent(product -> product.setPrice(newPrice));
            fileUtil.writeProductsToFile(products);
            return productOpt;
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }



//    public Optional<Product> findProductById(Long id) {
//        try {
//            List<Product> products = objectMapper.readValue(new File(DATABASE_FILE), new TypeReference<List<Product>>() {});
//            return products.stream().filter(product -> product.getId().equals(id)).findFirst();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Optional.empty();
//    }
}
