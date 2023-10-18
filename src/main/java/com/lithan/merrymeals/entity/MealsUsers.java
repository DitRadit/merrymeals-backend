package com.lithan.merrymeals.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lithan.merrymeals.model.AuthenticationProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealsUsers {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String userName;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String phoneNumber;
    private String address;
    private String gender;
    private String age;
    private String illness;
    @Column(nullable = true)
    private String nurse;
    private String certificateName;
    @Lob
    @JsonIgnore
    private byte[] certificateFile;
    private String profilePicsName;
    @Lob
    @JsonIgnore
    private byte[] profilePicsFile;
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthenticationProvider authProvider;
    
    public MealsUsers(String userName) {
        this.userName = userName;
    }
    
}
