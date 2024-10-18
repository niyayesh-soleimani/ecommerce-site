package com.example.myStore.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(sequenceName = "user_seq", name = "user_seq", allocationSize = 1)
public class User {

    @Id
    @GeneratedValue(generator = "user_seq")
    private Long id;
    private String email;
    private String password;
    private String roleName;
}
