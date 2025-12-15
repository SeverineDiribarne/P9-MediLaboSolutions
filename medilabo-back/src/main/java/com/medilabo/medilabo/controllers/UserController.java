package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.dto.UserPublicDTO;
import com.medilabo.medilabo.services.userService.IUserService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserPublicDTO> getUsers() {
        return userService.findAll()
                .stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getAuthorities().stream()
                                .map(Object::toString)
                                .toList()))
                .toList();
    }

}
