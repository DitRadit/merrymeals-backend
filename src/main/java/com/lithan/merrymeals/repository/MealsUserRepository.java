package com.lithan.merrymeals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lithan.merrymeals.entity.MealsUsers;


public interface MealsUserRepository extends JpaRepository <MealsUsers, Long> {

    Optional<MealsUsers> findByUserName(String userName);
    
    Optional<MealsUsers> findByCertificateName(String certificateName);

    Optional<MealsUsers> findByProfilePicsName(String profilePicsName);


    Optional<MealsUsers> findByEmail(String email);

    MealsUsers findByCertificateNameAndProfilePicsName(String certificateName, String profilePicsName);

    @Query("SELECT u FROM MealsUsers u WHERE u.role = 'PENDING'")
    List<MealsUsers> findPendingUsers();

    
    @Query("SELECT u FROM MealsUsers u WHERE u.role = 'PENDING1'")
    List<MealsUsers> findPendingCaregiver();

     @Query("SELECT u FROM MealsUsers u WHERE u.role = 'PENDING2'")
    List<MealsUsers> findPendingPartner();

    @Query("SELECT u FROM MealsUsers u WHERE u.userName LIKE %:keyword% OR u.nurse LIKE %:keyword%")
    List<MealsUsers> searchUsersByKeyword(@Param("keyword") String keyword);
}
