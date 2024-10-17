package com.example.myStore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.myStore.entity.Product;

public interface ProductDA extends JpaRepository<Product, Long> {
}


