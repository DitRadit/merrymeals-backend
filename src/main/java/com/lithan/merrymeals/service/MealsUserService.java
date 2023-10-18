package com.lithan.merrymeals.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.repository.MealsUserRepository;
import com.lithan.merrymeals.util.ImageUtil;

@Service
public class MealsUserService {
    
    @Autowired
    private MealsUserRepository userRepo;

    
    public List<MealsUsers> getAllUser() {
        return userRepo.findAll();
    }

    public Optional<MealsUsers> getUserById(long id) {
        return userRepo.findById(id);
    }

    public MealsUsers createUser(MealsUsers user) {
        Optional<MealsUsers> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        return userRepo.save(user);
    }
    

    public String uploadImage(Long id, MultipartFile certificate, MultipartFile profilePics) throws IOException {
        Optional<MealsUsers> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            MealsUsers user = optionalUser.get();
            user.setCertificateName(certificate.getOriginalFilename());
            user.setCertificateFile(ImageUtil.compressImage(certificate.getBytes()));
            user.setProfilePicsName(profilePics.getOriginalFilename());
            user.setProfilePicsFile(ImageUtil.compressImage(profilePics.getBytes()));
            userRepo.save(user);
            return certificate.getOriginalFilename() + " " + profilePics.getOriginalFilename();
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
    

    public String uploadImageByEmail(String email, MultipartFile certificate, MultipartFile profilePics) throws IOException {
        Optional<MealsUsers> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            MealsUsers user = optionalUser.get();
            user.setCertificateName(certificate.getOriginalFilename());
            user.setCertificateFile(ImageUtil.compressImage(certificate.getBytes()));
            user.setProfilePicsName(profilePics.getOriginalFilename());
            user.setProfilePicsFile(ImageUtil.compressImage(profilePics.getBytes()));
            userRepo.save(user);
            return certificate.getOriginalFilename() + " " + profilePics.getOriginalFilename();
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }


    public byte[] getCertificateName(String certificateName){
        Optional<MealsUsers> dbMealsUser = userRepo.findByCertificateName(certificateName);
        byte[] images=ImageUtil.decompressImage(dbMealsUser.get().getCertificateFile());
        return images;
    }

      public byte[] getProfilePicsName(String profilePicsName){
        Optional<MealsUsers> dbMealsUser = userRepo.findByProfilePicsName(profilePicsName);
        byte[] images=ImageUtil.decompressImage(dbMealsUser.get().getProfilePicsFile());
        return images;
    }

    public byte[] getCertificateByUserId(Long id) {
        Optional<MealsUsers> dbMealsUser = userRepo.findById(id);
        if (dbMealsUser.isPresent()) {
            MealsUsers user = dbMealsUser.get();
            if (user.getCertificateName() != null) {
                return ImageUtil.decompressImage(user.getCertificateFile());
            } else {
                throw new RuntimeException("Certificate not found for user with ID: " + id);
            }
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
    
    public byte[] getProfilePicsByUserId(Long id) {
        Optional<MealsUsers> dbMealsUser = userRepo.findById(id);
        if (dbMealsUser.isPresent()) {
            MealsUsers user = dbMealsUser.get();
            if (user.getProfilePicsName() != null) {
                return ImageUtil.decompressImage(user.getProfilePicsFile());
            } else {
                throw new RuntimeException("Profile picture not found for user with ID: " + id);
            }
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
    
    public MealsUsers updateUserById(Long id, MealsUsers updatedUser) {
        Optional<MealsUsers> existingUserOptional = userRepo.findById(id);
        if (existingUserOptional.isPresent()) {
            MealsUsers existingUser = existingUserOptional.get();
            
            // Update fields only if they are not null in the updatedUser object
            if (updatedUser.getUserName() != null) {
                existingUser.setUserName(updatedUser.getUserName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null) {
                existingUser.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getAddress() != null) {
                existingUser.setAddress(updatedUser.getAddress());
            }
            if (updatedUser.getGender() != null) {
                existingUser.setGender(updatedUser.getGender());
            }
            if (updatedUser.getAge() != null) {
                existingUser.setAge(updatedUser.getAge());
            }
            if (updatedUser.getIllness() != null) {
                existingUser.setIllness(updatedUser.getIllness());
            }
            if (updatedUser.getNurse() != null) {
                existingUser.setNurse(updatedUser.getNurse());
            }
            if (updatedUser.getRole() != null) {
                existingUser.setRole(updatedUser.getRole());
            }

            if (updatedUser.getCertificateName() != null) {
                existingUser.setCertificateName(updatedUser.getCertificateName());
            }
            if (updatedUser.getCertificateFile() != null) {
                existingUser.setCertificateFile(updatedUser.getCertificateFile());
            }
            if (updatedUser.getProfilePicsName() != null) {
                existingUser.setProfilePicsName(updatedUser.getProfilePicsName());
            }
            if (updatedUser.getProfilePicsFile() != null) {
                existingUser.setProfilePicsFile(updatedUser.getProfilePicsFile());
            }
            
            
            // Save the updated user to the database
            return userRepo.save(existingUser);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
    

    public void deleteUser(long id) {
        userRepo.deleteById(id);
    }

    public List<MealsUsers> findPendingUsers() {
        return userRepo.findPendingUsers();
    }

     public List<MealsUsers> findPendingCaregiver() {
        return userRepo.findPendingCaregiver();
    }

    public List<MealsUsers> findPendingPartner() {
        return userRepo.findPendingPartner();
    }

    public List<MealsUsers> searchUsersByKeyword(String keyword) {
        return userRepo.searchUsersByKeyword(keyword);
    }

}
