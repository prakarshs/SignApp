package com.Security.SignApp.Repository;

import com.Security.SignApp.Entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
   Optional<UserVerification> findByVerificationToken(String verificationToken);
}
