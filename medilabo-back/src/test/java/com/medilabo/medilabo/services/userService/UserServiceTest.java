package com.medilabo.medilabo.services.userService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medilabo.medilabo.dto.UserPublicDTO;
import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.model.UserAuthorities;
import com.medilabo.medilabo.repositories.IUserRepository;

class UserServiceTest {

    @Test
    @DisplayName("saveUser encode le mot de passe et sauvegarde")
    void saveUser_encodes() {
        IUserRepository repo = mock(IUserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService svc = new UserService(repo, encoder);

        User in = new User(0L, "john@doe", "pwd", true, false, true, true, UserAuthorities.USER);
        when(encoder.encode("pwd")).thenReturn("ENCODED");
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User out = svc.saveUser(in);
        assertEquals("ENCODED", out.getPassword());
        verify(encoder).encode("pwd");
        verify(repo).save(any(User.class));
    }

    @Test
    @DisplayName("findAll retourne des UserPublicDTO avec rôles mappés")
    void findAll_maps() {
        IUserRepository repo = mock(IUserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService svc = new UserService(repo, encoder);

        User u1 = new User(1L, "a@a", "p", true, false, true, true, UserAuthorities.ADMIN);
        User u2 = new User(2L, "b@b", "p", true, false, true, true, UserAuthorities.ADMIN_AND_USER);
        when(repo.findAll()).thenReturn(List.of(u1, u2));

        List<UserPublicDTO> out = svc.findAll();
        assertEquals(2, out.size());
        assertEquals("a@a", out.get(0).getUsername());
        assertTrue(out.get(0).getAuthorities().contains("ROLE_ADMIN"));
        assertEquals(2, out.get(1).getAuthorities().size());
        assertTrue(out.get(1).getAuthorities().contains("ROLE_ADMIN"));
        assertTrue(out.get(1).getAuthorities().contains("ROLE_USER"));
    }
}
