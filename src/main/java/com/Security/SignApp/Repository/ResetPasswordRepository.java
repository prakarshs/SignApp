package com.Security.SignApp.Repository;

import com.Security.SignApp.Entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword,Long> {
   Optional<ResetPassword> findByResetToken(String resetToken);
}
