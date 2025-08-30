package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.entity.User;
import com.aditi.menu.menu_backend.repository.UserRepository;
import com.aditi.menu.menu_backend.specs.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSpecification userSpecification;

    public Page<User> getAllUsers(Pageable pageable, String search, Integer status) {
        return userRepository.findAll(userSpecification.getUsers(search, status), pageable);
    }
}
