package com.medilabo.medilabo.services.userService;

import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.repositories.IUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String username) {
        System.out.println("je passe dans la methode findByEmail du UserService");
        return userRepository.findByEmail(username);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @PostConstruct
    public void initUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("sedi77.sd@gmail.com", passwordEncoder.encode("?SD29@ds!")));
        //users.add(new User("user2@example.com", passwordEncoder.encode("password2")));
        // Ajoutez d'autres utilisateurs ici

        for (User user : users) {
            if (userRepository.findByEmail(user.getEmail()) == null) {
                userRepository.save(user);
            }
        }
    }
}