package com.medilabo.medilabo.services.userService;

import com.medilabo.medilabo.model.User;

public interface IUserService {

    public User findByUsername(String username);
}
