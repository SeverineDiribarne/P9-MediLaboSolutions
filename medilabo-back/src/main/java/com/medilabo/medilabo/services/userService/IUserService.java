package com.medilabo.medilabo.services.userService;

import java.util.List;
import com.medilabo.medilabo.dto.UserPublicDTO;
import com.medilabo.medilabo.model.User;

public interface IUserService {

    User findByEmail(String username);

    User saveUser(User user);

    List<UserPublicDTO> findAll();
}
