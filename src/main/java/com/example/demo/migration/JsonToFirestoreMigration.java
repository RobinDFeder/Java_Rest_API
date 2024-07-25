package com.example.demo.migration;

import com.example.demo.products.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JsonToFirestoreMigration {

    public static void main(String[] args) throws IOException {
        // Initialize Firebase
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/Credentials/serviceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);

        // Get Firestore instance
        Firestore db = FirestoreClient.getFirestore();

        // Read JSON file
        ObjectMapper mapper = new ObjectMapper();
        List<Product> products = mapper.readValue(Paths.get("./database.json").toFile(),
                mapper.getTypeFactory().constructCollectionType(List.class, Product.class));

        // Write to Firestore
        WriteBatch batch = db.batch();
        for (Product product : products) {
            batch.set(db.collection("products").document(product.getId().toString()), product);
        }

        // Commit the batch
        try {
            batch.commit().get();
            System.out.println("Migration completed successfully!");
        } catch (Exception e) {
            System.err.println("Error during migration: " + e.getMessage());
        }
    }
}