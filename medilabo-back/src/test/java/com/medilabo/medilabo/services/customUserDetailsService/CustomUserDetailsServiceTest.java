package com.medilabo.medilabo.services.customUserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.lang.reflect.Field;

import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.model.UserAuthorities;
import com.medilabo.medilabo.repositories.IUserRepository;

class CustomUserDetailsServiceTest {

    @Test
    @DisplayName("loadUserByUsername return CustomUserDetails when user exists")
    void loadUser_ok() {
    IUserRepository repo = mock(IUserRepository.class);
    CustomUserDetailsService svc = new CustomUserDetailsService();
    injectPrivateField(svc, "userRepository", repo);

        User user = new User(1L, "john@doe", "pwd", true, false, true, true, UserAuthorities.USER);
        when(repo.findByEmail("john@doe")).thenReturn(user);

        var details = svc.loadUserByUsername("john@doe");
        assertEquals("john@doe", details.getUsername());
        assertTrue(details.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when null")
    void loadUser_notFound() {
    IUserRepository repo = mock(IUserRepository.class);
    CustomUserDetailsService svc = new CustomUserDetailsService();
    injectPrivateField(svc, "userRepository", repo);

        when(repo.findByEmail("missing")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("missing"));
    }

        private static void injectPrivateField(Object target, String fieldName, Object value) {
            try {
                Field f = target.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
}
