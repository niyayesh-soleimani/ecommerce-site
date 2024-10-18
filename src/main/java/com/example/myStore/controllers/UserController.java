package com.example.myStore.controllers;

import com.example.myStore.entity.User;
import com.example.myStore.entity.UserDTO;
import com.example.myStore.repository.UserDA;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;


@Controller
public class UserController {

    @Autowired
    private UserDA userDA; // Repository for User data access

    // Show the login page
    @GetMapping({"", "/", "login"})
    public String showLoginPage(Model model) {
        model.addAttribute("userDTO", new UserDTO()); // Initialize UserDTO for the login form
        return "user/login"; // Return the login view
    }

    // Show the registration page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDTO", new UserDTO()); // Initialize UserDTO for the registration form
        return "user/register"; // Return the registration view
    }

    // Handle user registration
    @PostMapping("/register")
    public String register(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/register"; // Return to the registration page if there are validation errors
        }

        // Create and save the new user
        User user = new User();
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRoleName(userDTO.getRoleName());

        if (user.getRoleName() == null || user.getRoleName().isEmpty()) {
            user.setRoleName("user");
        }
        userDA.save(user);
        // Add success message and redirect to login page
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        return "redirect:/login";
    }

    // Handle user login

    @PostMapping("/login")
    public String login(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, Model model, HttpSession session) {
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        // Check if the user exists with the given email and password
        User user = userDA.findByEmailAndPassword(email, password);
        if (user == null) {
            // If user is not found, reject the email field with an error
            bindingResult.rejectValue("email", "error", "Invalid email or password");
            return "user/login"; // Return to the login page with an error message
        } else {
            String roleName = user.getRoleName();  // Get role from user
            session.setAttribute("roleName", roleName);  // Store roleName in session

            return "redirect:/products"; // Redirect to the products page after successful login
        }
    }


}
