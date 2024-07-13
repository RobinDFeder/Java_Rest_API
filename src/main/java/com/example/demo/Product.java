package com.example.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    private Long id;
    private String name;
    private Double price;

    //Consturctor
    public Product() {}

    public Product(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

        @JsonProperty("id")
        public Long getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Long id) {
            this.id = id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("price")
        public Double getPrice() {
            return price;
        }

        @JsonProperty("price")
        public void setPrice(Double price) {
            this.price = price;
        }
}
