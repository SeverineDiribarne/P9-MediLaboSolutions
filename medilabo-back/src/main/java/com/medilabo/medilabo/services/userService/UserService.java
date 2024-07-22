package com.medilabo.medilabo.services.userService;

import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.repositories.IUserRepository;

public class UserService implements IUserService {

    private IUserRepository customerRepository;

    public User findByUsername(String username) {
        return customerRepository.findByEmail(username);
    }
}