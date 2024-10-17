package com.example.myStore.repository;
import com.example.myStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserDA extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

}
