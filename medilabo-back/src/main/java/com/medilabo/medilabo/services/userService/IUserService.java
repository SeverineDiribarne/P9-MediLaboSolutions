package com.medilabo.medilabo.services.userService;

import com.medilabo.medilabo.model.User;

public interface IUserService {

    User findByEmail(String username);

    User saveUser(User user);
}
