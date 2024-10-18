package com.example.myStore.entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDTO {
    @NotEmpty(message = "The email is required")
    @Email(message = "The email must be valid")
    private String email;
    @NotEmpty(message = "The password is required")
    @Size(min = 8, max = 12, message = "The password must be between 8 and 12 characters")
    private String password;
    @Setter(AccessLevel.NONE)
    private String roleName ;

}

