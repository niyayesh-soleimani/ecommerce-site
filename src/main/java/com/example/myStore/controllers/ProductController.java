package com.example.myStore.controllers;

import com.example.myStore.entity.Product;
import com.example.myStore.entity.ProductDTO;
import com.example.myStore.repository.ProductDA;
import com.example.myStore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService; // Service for handling product logic

    @Autowired
    private ProductDA productDA; // Repository for Product data access

    // Show the list of products with pagination
    @GetMapping({"", "/"})
    public String showProductList(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findAllProducts(pageable);
        model.addAttribute("products", productPage.getContent()); // Add product list to model
        model.addAttribute("currentPage", page); // Add current page number
        model.addAttribute("totalPages", productPage.getTotalPages()); // Add total number of pages
        return "products/index.html"; // Return the product list view
    }

    // Show the page to create a new product
    @GetMapping({"/create"})
    public String showCreatePage(Model model) {
        ProductDTO productDTO = new ProductDTO(); // Initialize ProductDTO for the create form
        model.addAttribute("productDTO", productDTO);
        return "products/createProduct.html"; // Return the create product view
    }

    // Handle product creation
    @PostMapping({"/create"})
    public String createProduct(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                BindingResult result, Model model) {
        if (productDTO.getImageFile() == null || productDTO.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDTO", "imageFile", "The image file is required."));
            return "products/createProduct.html"; // Return to create product page if image file is missing
        }
        if (result.hasErrors()) {
            return "products/createProduct.html"; // Return to create product page if there are validation errors
        }

        // Process the uploaded image file
        MultipartFile image = productDTO.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + " ـ " + image.getOriginalFilename();
        try {
            String uploadDir = "static/public/images/";
            Path uploadPath = Paths.get(uploadDir);
            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // Copy the uploaded image file to the specified directory
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            result.addError(new FieldError("productDTO", "imageFile", "File upload failed: " + ex.getMessage()));
            return "products/createProduct.html"; // Return to create product page if file upload fails
        }

        // Create and save the new product
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setCreateAt(createdAt);
        product.setImageFileName(storageFileName);
        productDA.save(product);
        return "redirect:/products"; // Redirect to product list after successful creation
    }

    // Show the page to edit an existing product
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam Long id) {
        try {
            Product product = productDA.findById(id).get(); // Retrieve product by ID
            model.addAttribute("product", product);
            ProductDTO productDTO = new ProductDTO();
            // Initialize ProductDTO with existing product data
            productDTO.setName(product.getName());
            productDTO.setBrand(product.getBrand());
            productDTO.setCategory(product.getCategory());
            productDTO.setPrice(product.getPrice());
            productDTO.setDescription(product.getDescription());
            model.addAttribute("productDTO", productDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/products"; // Redirect to product list on error
        }
        return "products/editProduct.html"; // Return the edit product view
    }

    // Handle product update
    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam Long id,
                                @Valid @ModelAttribute ProductDTO productDTO, BindingResult result) {
        try {
            Product product = productDA.findById(id).get(); // Retrieve product by ID
            model.addAttribute("product", product);
            if (result.hasErrors()) {
                return "products/editProduct.html"; // Return to edit product page if there are validation errors
            }

            // Check if a new image file is uploaded
            if (!productDTO.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                // Delete the old image file
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                // Process the new uploaded image file
                MultipartFile image = productDTO.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "-" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName); // Update the image file name
            }

            // Update product details
            product.setName(productDTO.getName());
            product.setBrand(productDTO.getBrand());
            product.setCategory(productDTO.getCategory());
            product.setPrice(productDTO.getPrice());
            product.setDescription(productDTO.getDescription());
            productDA.save(product); // Save the updated product
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "redirect:/products"; // Redirect to product list after successful update
    }

    // Handle product deletion
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) {
        try {
            Product product = productDA.findById(id).orElse(null); // Retrieve product by ID
            if (product != null) {
                Path imagePath = Paths.get("public/images/" + product.getImageFileName());
                // Delete the associated image file
                try {
                    Files.deleteIfExists(imagePath);
                } catch (Exception ex) {
                    System.out.println("Error deleting image: " + ex.getMessage());
                }
                productDA.delete(product); // Delete the product
            }
        } catch (Exception ex) {
            System.out.println("Error deleting product: " + ex.getMessage());
        }
        return "redirect:/products"; // Redirect to product list after deletion
    }
}
