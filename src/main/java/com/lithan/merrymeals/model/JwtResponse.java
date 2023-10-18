package com.lithan.merrymeals.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtResponse implements Serializable {

    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String userName;
    private String email;
    private String role;

    public JwtResponse(
        String accessToken,
        String refreshToken,
        String email,
        String userName,
        String role){
            this.email = email;
            this.userName = userName;
            this.token = accessToken;
            this.refreshToken = refreshToken;
            this.role = role;
        }
}
