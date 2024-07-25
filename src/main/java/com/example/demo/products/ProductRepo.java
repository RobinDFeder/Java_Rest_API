package com.example.demo.products;

//import com.example.demo.FileUtil;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


//import java.io.IOException;
import java.util.List;
import java.util.Optional;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;

//@Repository
//public class ProductRepo {
//
//    private final FileUtil fileUtil;
//    private final Lock lock = new ReentrantLock();
//    private Long nextId = 1L;
//
//    @Autowired
//    public ProductRepo(FileUtil fileUtil) {
//        this.fileUtil = fileUtil;
//        try {
//            List<Product> products = fileUtil.readProductsFromFile();
//            if (!products.isEmpty()) {
//                nextId = products.stream().mapToLong(Product::getId).max().orElse(0) + 1;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<Product> findAll() {
//        lock.lock();
//        try {
//            return fileUtil.readProductsFromFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return List.of();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public Optional<Product> findById(Long id) {
//        lock.lock();
//        try {
//            return fileUtil.readProductsFromFile().stream().filter(product -> product.getId().equals(id)).findFirst();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public Optional<Product> findByName(String name){
//        lock.lock();
//        try{
//            return fileUtil.readProductsFromFile().stream().filter(product -> product.getName().equals(name)).findFirst();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public Product save(Product product) {
//        lock.lock();
//        try {
//            List<Product> products = fileUtil.readProductsFromFile();
//            if (product.getId() == null) {
//                product.setId(nextId++);
//            }
//            products.add(product);
//            fileUtil.writeProductsToFile(products);
//            return product;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public Optional<Product> updatePrice(Long id, Double newPrice) {
//        lock.lock();
//        try {
//            List<Product> products = fileUtil.readProductsFromFile();
//            Optional<Product> productOpt = products.stream().filter(product -> product.getId().equals(id)).findFirst();
//            productOpt.ifPresent(product -> product.setPrice(newPrice));
//            fileUtil.writeProductsToFile(products);
//            return productOpt;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        } finally {
//            lock.unlock();
//        }
//    }
//}


// Firebase implementation -----

@Repository
public class ProductRepo {

    private static final String COLLECTION_NAME = "products";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = getFirestore().collection(COLLECTION_NAME).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                products.add(document.toObject(Product.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching products", e);
        }
        return products;
    }

    public Optional<Product> findById(Long id) {
        ApiFuture<DocumentSnapshot> future = getFirestore().collection(COLLECTION_NAME).document(id.toString()).get();
        try {
            DocumentSnapshot document = future.get();
            return document.exists() ? Optional.of(document.toObject(Product.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching product", e);
        }
    }

    public Optional<Product> findByName(String name) {
        ApiFuture<QuerySnapshot> future = getFirestore().collection(COLLECTION_NAME).whereEqualTo("name", name).limit(1).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return !documents.isEmpty() ? Optional.of(documents.get(0).toObject(Product.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching product by name", e);
        }
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(generateNextId());
        }
        ApiFuture<WriteResult> future = getFirestore().collection(COLLECTION_NAME).document(product.getId().toString()).set(product);
        try {
            future.get();
            return product;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error saving product", e);
        }
    }

    public Optional<Product> updatePrice(Long id, Double newPrice) {
        DocumentReference docRef = getFirestore().collection(COLLECTION_NAME).document(id.toString());
        ApiFuture<WriteResult> future = docRef.update("price", newPrice);
        try {
            future.get();
            return findById(id);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error updating product price", e);
        }
    }

    private Long generateNextId() {
        ApiFuture<QuerySnapshot> future = getFirestore().collection(COLLECTION_NAME)
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                Long lastId = documents.get(0).getLong("id");
                return lastId + 1;
            } else {
                return 1L;
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error generating next id", e);
        }
    }
}
