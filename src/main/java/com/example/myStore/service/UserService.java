package com.example.myStore.service;
import com.example.myStore.repository.UserDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    @Autowired
      UserDA userDA;

}
