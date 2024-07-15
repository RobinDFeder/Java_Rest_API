package com.example.demo;

import com.example.demo.products.Product;
import com.example.demo.products.ProductController;
import com.example.demo.products.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productService.findAllProducts()).thenReturn(products);

        List<Product> result = productController.getAllProducts();

        assertEquals(products, result);
        verify(productService).findAllProducts();
    }

    @Test
    void getProductById_ExistingProduct() {
        Long id = 1L;
        Product product = new Product();
        when(productService.findProductById(id)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void getProductById_NonExistingProduct() {
        Long id = 1L;
        when(productService.findProductById(id)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addProduct_Success() {
        Product product = new Product();
        when(productService.addProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.addProduct(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void addProduct_Failure() {
        Product product = new Product();
        when(productService.addProduct(product)).thenReturn(null);

        ResponseEntity<Product> response = productController.addProduct(product);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void changeProductPrice_Success() {
        Long id = 1L;
        Double newPrice = 10.0;
        Product updatedProduct = new Product();
        Map<String, Double> request = new HashMap<>();
        request.put("price", newPrice);

        when(productService.changeProductPrice(id, newPrice)).thenReturn(Optional.of(updatedProduct));

        ResponseEntity<Product> response = productController.changeProductPrice(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
    }

    @Test
    void changeProductPrice_ProductNotFound() {
        Long id = 1L;
        Double newPrice = 10.0;
        Map<String, Double> request = new HashMap<>();
        request.put("price", newPrice);

        when(productService.changeProductPrice(id, newPrice)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.changeProductPrice(id, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void changeProductPrice_MissingPrice() {
        Long id = 1L;
        Map<String, Double> request = new HashMap<>();

        ResponseEntity<Product> response = productController.changeProductPrice(id, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getProductByName_ExistingProduct() {
        String name = "TestProduct";
        Product product = new Product();
        product.setName(name);
        when(productService.findProductByName(name)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProductByName(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService).findProductByName(name);
    }

    @Test
    void getProductByName_NonExistingProduct() {
        String name = "NonExistingProduct";
        when(productService.findProductByName(name)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getProductByName(name);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).findProductByName(name);
    }
}