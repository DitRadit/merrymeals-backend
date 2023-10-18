package com.lithan.merrymeals.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.exception.ErrorResponse;
import com.lithan.merrymeals.model.JwtResponse;
import com.lithan.merrymeals.model.LoginRequest;
import com.lithan.merrymeals.model.RefreshTokenRequest;
import com.lithan.merrymeals.model.SignUpRequest;
import com.lithan.merrymeals.security.jwt.JwtUtils;
import com.lithan.merrymeals.security.service.UserDetailsImpl;
import com.lithan.merrymeals.security.service.UserDetailsServiceImpl;
import com.lithan.merrymeals.service.MealsUserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MealsUserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

 @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                    
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshJwtToken(authentication);
            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            return ResponseEntity.ok().body(new JwtResponse(token, refreshToken, principal.getUsername(), principal.getEmail(), principal.getRole()));
        } catch (AuthenticationException e) {
            // Handle authentication failure (incorrect credentials)
            Map<String, String> response = new HashMap<>();
            response.put("error", "Unauthorized: Incorrect email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    

    @PostMapping("/signup")
    public MealsUsers signupUser(@RequestBody SignUpRequest request){
        MealsUsers users = new MealsUsers();
        users.setUserName(request.getUserName());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setEmail(request.getEmail());
        users.setPhoneNumber(request.getPhoneNumber());
        users.setAddress(request.getAddress());
        users.setAge(request.getAge());
        users.setGender(request.getGender());
        users.setIllness(request.getIllness());
        users.setNurse(request.getNurse());
        users.setRole("PENDING");
        MealsUsers created = userService.createUser(users);
        return created;
    }

      @PostMapping("/signupcaregiver")
    public MealsUsers signupCaregiver(@RequestBody SignUpRequest request){
        MealsUsers users = new MealsUsers();
        users.setUserName(request.getUserName());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setEmail(request.getEmail());
        users.setPhoneNumber(request.getPhoneNumber());
        users.setAddress(request.getAddress());
        users.setAge(request.getAge());
        users.setGender(request.getGender());
        users.setIllness(request.getIllness());
        users.setNurse(request.getNurse());
        users.setRole("PENDING1");
        MealsUsers created = userService.createUser(users);
        return created;
    }

    @PostMapping("/signuppartner")
    public MealsUsers signupPartner(@RequestBody SignUpRequest request){
        MealsUsers users = new MealsUsers();
        users.setUserName(request.getUserName());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setEmail(request.getEmail());
        users.setPhoneNumber(request.getPhoneNumber());
        users.setAddress(request.getAddress());
        users.setAge(request.getAge());
        users.setGender(request.getGender());
        users.setIllness(request.getIllness());
        users.setNurse(request.getNurse());
        users.setRole("PENDING2");
        MealsUsers created = userService.createUser(users);
        return created;
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        String token = request.getRefreshToken();
        boolean valid = jwtUtils.validateJwtToken(token);
        if (!valid){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        String newToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(newToken, refreshToken, username, userDetailsImpl.getEmail(), userDetailsImpl.getRole()));
    }

}
