package com.lithan.merrymeals.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.repository.MealsUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MealsUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MealsUsers user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    
        return UserDetailsImpl.build(user);
    }
    
    
    
    
    
}
