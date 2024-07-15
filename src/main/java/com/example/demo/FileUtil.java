package com.example.demo;

import com.example.demo.products.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class FileUtil {

    private static final String FILE_PATH = "database.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public List<Product> readProductsFromFile() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
            mapper.writeValue(file, List.of());
        }
        return mapper.readValue(file, new TypeReference<List<Product>>() {});
    }

    public void writeProductsToFile(List<Product> products) throws IOException {
        mapper.writeValue(new File(FILE_PATH), products);
    }
}
