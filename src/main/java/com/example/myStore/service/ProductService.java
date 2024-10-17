package com.example.myStore.service;
import com.example.myStore.entity.Product;
import com.example.myStore.repository.ProductDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProductService {
    @Autowired
    ProductDA productDA;

    public Page<Product> findAllProducts(Pageable pageable) {
        return productDA.findAll(pageable);
    }



}
