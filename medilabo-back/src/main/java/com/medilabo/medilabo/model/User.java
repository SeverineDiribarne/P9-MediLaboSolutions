package com.medilabo.medilabo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, name = "active_account")
    private boolean accountIsActive;

    @Column(nullable = false, name = "locked_account")
    private boolean accountIsLocked;

    @Column(nullable = false, name = "credentials_non_expired")
    private boolean isCredentialsNonExpired;

    @Column(nullable = false, name = "enabled_account")
    private boolean isEnabled;

    @Column(nullable = false, name = "authorities")
    private UserAuthorities userAuthorities;

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return (accountIsActive == true);
    }

    @Override
    public boolean isAccountNonLocked() {
        return (accountIsLocked == false);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        switch (userAuthorities) {
            case USER:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserAuthorities.USER));
                break;

            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserAuthorities.ADMIN));
                break;

            case ADMIN_AND_USER:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserAuthorities.ADMIN));
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserAuthorities.USER));
                break;

            default:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserAuthorities.USER));
                break;
        }
        return authorities;
    }
}
