package com.lithan.merrymeals.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lithan.merrymeals.entity.MealsUsers;

import lombok.Data;

@Data
public class UserDetailsImpl implements UserDetails {

    private String userName;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String role;

    

    public UserDetailsImpl(
            long id, String userName, 
            String email, 
            String password, 
            String role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static UserDetailsImpl build(MealsUsers user){
        return new UserDetailsImpl(user.getId(), 
        user.getEmail(), 
        user.getUserName(), 
        user.getPassword(), 
        user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(StringUtils.hasText(role)){
            String[] splits = role.replaceAll(" ", "").split(",");
            for (String string : splits) {
                authorities.add(new SimpleGrantedAuthority(string));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
       return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
       return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }
    
}
