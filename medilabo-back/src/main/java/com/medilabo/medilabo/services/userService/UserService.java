package com.medilabo.medilabo.services.userService;

import com.medilabo.medilabo.dto.UserPublicDTO;
import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.repositories.IUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<UserPublicDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()))
                .toList();
    }
}