package com.example.sport.service;

import com.example.sport.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User findByEmailAndPassword(String email, String password);
    User saveUser(User user);
    List<User> getAllUsers();
    User findById(Long id);
    void deleteUser(Long id);
    User getUserById(Long id);
    void save(User user);
    void deleteUserById(Long id);
    public void updateUser(User updatedUser);
    public Optional<User> findByEmail(String email);

    
}
