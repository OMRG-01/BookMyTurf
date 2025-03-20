package com.example.sport.service;

import com.example.sport.model.Role;
import com.example.sport.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);  // Fetch the role by ID
    }
}
