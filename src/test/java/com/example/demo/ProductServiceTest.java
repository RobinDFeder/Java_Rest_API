package com.example.demo;

import com.example.demo.products.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.demo.products.Product;
import com.example.demo.products.ProductRepo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testFindAllProducts_EmptyList() {
        // Mock productRepo to return an empty list
        List<Product> emptyList = Collections.emptyList();
        when(productRepo.findAll()).thenReturn(emptyList);

        // Call the service method
        List<Product> products = productService.findAllProducts();

        // Assert that an empty list is returned
        assertEquals(emptyList, products);
    }

    @Test
    public void testFindAllProducts_NotEmptyList() {
        // Mock productRepo to return a list with products
        List<Product> productList = Arrays.asList(new Product(1L, "Product 1", 10.99));
        when(productRepo.findAll()).thenReturn(productList);

        // Call the service method
        List<Product> products = productService.findAllProducts();

        // Assert that the returned list matches the mocked list
        assertEquals(productList, products);
    }

    @Test
    public void testFindProductById_Found() {
        // Mock productRepo to return an Optional with a product
        Long productId = 2L;
        Product expectedProduct = new Product(productId, "Product 2", 15.50);
        when(productRepo.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // Call the service method
        Optional<Product> actualProduct = productService.findProductById(productId);

        // Assert that the returned Optional contains the expected product
        assertTrue(actualProduct.isPresent());
        assertEquals(expectedProduct, actualProduct.get());
    }

    @Test
    public void testFindProductById_NotFound() {
        // Mock productRepo to return an empty Optional
        Long productId = 10L;
        when(productRepo.findById(productId)).thenReturn(Optional.empty());

        // Call the service method
        Optional<Product> actualProduct = productService.findProductById(productId);

        // Assert that the returned Optional is empty
        assertFalse(actualProduct.isPresent());
    }

    // Similar tests can be written for addProduct and changeProductPrice methods

}
