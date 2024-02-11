package com.Security.SignApp.Service;

import com.Security.SignApp.Entity.UserEntity;
import com.Security.SignApp.Model.MailTemplate;
import com.Security.SignApp.Model.ResetPasswordRequest;
import com.Security.SignApp.Model.UserRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserEntity registerUser(UserRequest userRequest, HttpServletRequest request);

    String saveToken(UserEntity user, String verificationToken);

    String sendMail(MailTemplate mailTemplate, String email);

    String activateUser(String verificationToken);

    String resendVerification(String userEmail, HttpServletRequest request);

    String resetPassword(ResetPasswordRequest resetPasswordRequest, HttpServletRequest request);

    String saveResetToken(UserEntity userEntity, String resetPasswordToken, String newPassword);

    String resetConfirm(String resetToken);
}
