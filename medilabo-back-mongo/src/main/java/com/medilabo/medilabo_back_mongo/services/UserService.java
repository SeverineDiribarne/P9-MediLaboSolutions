package com.medilabo.medilabo_back_mongo.services;

import com.medilabo.medilabo_back_mongo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

//    @Autowired
//    private IUserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    public User findByEmail(String username) {
//        System.out.println("je passe dans la methode findByEmail du UserService");
//        System.out.println(username);
//        return userRepository.findByEmail(username);
//    }
//
//    @Override
//    public User saveUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
}
