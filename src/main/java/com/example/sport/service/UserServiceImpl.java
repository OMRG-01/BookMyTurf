package com.example.sport.service;

import com.example.sport.model.User;
import com.example.sport.repository.UserRepository;
import com.example.sport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public void save(User user) {
        userRepository.save(user);  // Save the user to the database
    }
    public List<User> getAllUsers() {
        return userRepository.findByRoleId(2); // Only users with role 'USER' (role_id = 2)
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setMobileNumber(updatedUser.getMobileNumber());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setGender(updatedUser.getGender());
            userRepository.save(existingUser);
        }
    }
}
