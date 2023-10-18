package com.lithan.merrymeals.model;

import lombok.Data;

@Data
public class SignUpRequest {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String age;
    private String gender;
    private String illness;
    private String nurse;

}
