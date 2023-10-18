package com.lithan.merrymeals.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.exception.BadRequestException;
import com.lithan.merrymeals.security.service.UserDetailsImpl;
import com.lithan.merrymeals.service.MealsUserService;


@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class MealsUserController {

    
    @Autowired
    private MealsUserService userService;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<MealsUsers>> getAllUsers() {
        List<MealsUsers> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MealsUsers> getUserById(@PathVariable long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/user/create")
    public ResponseEntity<String> createUser(@RequestBody MealsUsers user,
        @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile,
        @RequestPart(value = "profilePicsFile", required = false) MultipartFile profilePicsFile) {
            try {
                // Validate input fields
                if (user.getUserName().length() < 8 || user.getUserName().length() > 20) {
                    throw new BadRequestException("Username must be between 8 and 20 characters.");
                }
    
                // Handle user and file data here
                // For example, save certificateFile and profilePicsFile to storage system
                // Save user data to the database
                MealsUsers createdUser = userService.createUser(user);
                return ResponseEntity.ok("User created successfully with ID: " + createdUser.getId());
            } catch (BadRequestException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }
    
    
    @PutMapping("/user/uploadimage/{id}")
    public ResponseEntity<String> uploadImage(
        @PathVariable Long id,
        @RequestParam("certificate") MultipartFile certificate,
        @RequestParam("profilePics") MultipartFile profilePics
) {
    try {
        String response = userService.uploadImage(id, certificate, profilePics);
        return ResponseEntity.ok("File Upload Successfully: " + response);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images: " + e.getMessage());
    }
}

@PutMapping("/user/uploadimageemail")
public ResponseEntity<String> uploadImage(
    @AuthenticationPrincipal UserDetailsImpl userDetails,
    @RequestParam("certificate") MultipartFile certificate,
    @RequestParam("profilePics") MultipartFile profilePics
) {
    try {
        // You can access the authenticated user's details using userDetails object.
        String userEmail = userDetails.getUsername();
        
        String response = userService.uploadImageByEmail(userEmail, certificate, profilePics);
        return ResponseEntity.ok("File Upload Successfully: " + response);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + userDetails.getUsername());
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images: " + e.getMessage());
    }
}

    @GetMapping("/user/getCertificate/{certificateName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getCertificateName(@PathVariable String certificateName){
        byte[] user=userService.getCertificateName(certificateName);
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.valueOf("image/png"))
            .body(user);
    }

    @GetMapping("/user/getProfilePics/{profilePicsName}")
    public ResponseEntity<?> getProfilePicsName(@PathVariable String profilePicsName){
        byte[] user=userService.getProfilePicsName(profilePicsName);
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.valueOf("image/png"))
            .body(user);
    }

    @GetMapping("user/{id}/certificate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> getCertificateByUserId(@PathVariable Long id) {
        try {
            byte[] certificate = userService.getCertificateByUserId(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Set the appropriate content type for the response
            return new ResponseEntity<>(certificate, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Handle not found scenario
        }
    }

    @GetMapping("user/{id}/profilePics")
    public ResponseEntity<byte[]> getProfilePicsByUserId(@PathVariable Long id) {
        try {
            byte[] profilePics = userService.getProfilePicsByUserId(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Set the appropriate content type for the response
            return new ResponseEntity<>(profilePics, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Handle not found scenario
        }
    }

    @PutMapping("user/edituser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateUserById(@PathVariable Long id, @RequestBody MealsUsers updatedUser) {
        try {
            MealsUsers user = userService.updateUserById(id, updatedUser);
            return ResponseEntity.ok("User with ID " + id + " updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("user/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<MealsUsers> getPendingUsers() {
        return userService.findPendingUsers();
    }

      @GetMapping("/user/pending1")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<MealsUsers> getPendingCaregiver() {
        return userService.findPendingCaregiver();
    }

      @GetMapping("/user/pending2")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<MealsUsers> getPendingPartner() {
        return userService.findPendingPartner();
    }

    @GetMapping("/user/search/{keyword}")
    public List<MealsUsers> searchUsersByKeyword(@PathVariable("keyword") String keyword) {
        return userService.searchUsersByKeyword(keyword);
    }

}
