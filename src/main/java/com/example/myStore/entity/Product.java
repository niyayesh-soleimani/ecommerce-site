package com.example.myStore.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "product")
@Table(name = "product")
@SequenceGenerator(sequenceName = "product-seq",name = "product_seq",allocationSize = 1)
public class Product {
    @Id
    @GeneratedValue(generator = "product_seq")
    private long id;
    private String name;
    private String brand;
    private String category;
    private double price;
    @Lob
    private String description;
    private Date createAt;
    private String imageFileName;



}
