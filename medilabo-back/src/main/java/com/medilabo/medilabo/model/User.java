package com.medilabo.medilabo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name= "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;
    
    private boolean activeAccount = false;

    public User(String mail, String password) {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.activeAccount;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.activeAccount;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.activeAccount;
    }

    @Override
    public boolean isEnabled() {
        return this.activeAccount;
    }
}
