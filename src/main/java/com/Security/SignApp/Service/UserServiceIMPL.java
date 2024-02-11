package com.Security.SignApp.Service;

import com.Security.SignApp.Constant.AppConstant;
import com.Security.SignApp.Entity.ResetPassword;
import com.Security.SignApp.Entity.UserEntity;
import com.Security.SignApp.Entity.UserVerification;
import com.Security.SignApp.Error.CustomError;
import com.Security.SignApp.Events.ResetPasswordEvent;
import com.Security.SignApp.Events.UserActivationEvent;
import com.Security.SignApp.Model.MailTemplate;
import com.Security.SignApp.Model.ResetPasswordRequest;
import com.Security.SignApp.Model.UserRequest;
import com.Security.SignApp.Repository.ResetPasswordRepository;
import com.Security.SignApp.Repository.UserRepository;
import com.Security.SignApp.Repository.UserVerificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;

@Service
@Log4j2
public class UserServiceIMPL implements UserService{



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public UserEntity registerUser(UserRequest userRequest, HttpServletRequest request) {
        log.info("CRATING USER...");
        UserEntity userEntity = UserEntity.builder()
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode( userRequest.getPassword()))
                .role(AppConstant.USER)
                .state(AppConstant.INACTIVE)
                .build();
        log.info("SAVING USER IN DB...");
        userRepository.save(userEntity);
        log.info("PUBLISHING ACTIVATION EVENT...");
        applicationEventPublisher.publishEvent(new UserActivationEvent(userEntity,generateTemplate(request)));

        return userEntity;
    }

    @Override
    public String saveToken(UserEntity user, String verificationToken) {
        log.info("CREATING USER-TOKEN...");

        UserVerification userVerification = UserVerification.builder()
                .user(user)
                .verificationToken(verificationToken)
                .expirationTime(Date.from(Instant.now().plusSeconds(AppConstant.EXPIRY_PERIOD)))
                .build();
        log.info("SAVING USER-TOKEN IN DB...");
        userVerificationRepository.save(userVerification);
        return "USER-TOKEN HAS BEEN SAVED IN DB.";
    }

    @Value("${spring.mail.username}")
    private String fromMail;
    @Override
    public String sendMail(MailTemplate mailTemplate, String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(mailTemplate.getSubject());
        simpleMailMessage.setText(mailTemplate.getContent());
        simpleMailMessage.setFrom(fromMail);
        log.info("SENDING MAIL...");
        javaMailSender.send(simpleMailMessage);
        return "MAIL WAS SENT !!";
    }

    @Override
    public String activateUser(String verificationToken) {
        log.info("CHECKING IF USER_TOKEN PRESENT...");
        UserVerification userVerification = userVerificationRepository.findByVerificationToken(verificationToken).orElseThrow(()->new CustomError(AppConstant.TOKEN_DOES_NOT_EXIST,AppConstant.TRY_WITH_A_DIFFERENT_TOKEN));
        log.info("TOKEN PRESENT! NOW CHECKING FOR EXPIRY...");
        if(Date.from(Instant.now()).getTime() - userVerification.getExpirationTime().getTime() < 0){
            log.info("TOKEN VALID! CHANGING STATE...");
            UserEntity userEntity =  userVerification.getUser();
            userEntity.setState(AppConstant.ACTIVE);
            userRepository.save(userEntity);
            return "USER WAS ACTIVATED !";
        }
        else{
            log.info("THE TOKEN HAS EXPIRED, DELETING ENTRY IN USER-TOKEN DB.");
            userVerificationRepository.delete(userVerification);
            return "TOKEN EXPIRED !";
        }

    }

    @Override
    public String resendVerification(String userEmail, HttpServletRequest request) {
        log.info("CHECKING IF EMAIL EXISTS...");
        UserEntity userEntity = userRepository.findByEmail(userEmail).orElseThrow(()->new CustomError("EMAIL DOES NOT EXIST","TRY WITH A DIFFERENT EMAIL"));
        log.info("IT EXISTS!, NOW PUBLISHING EVENT TO SEND VERIFICATION CODE.");

        applicationEventPublisher.publishEvent(new UserActivationEvent(userEntity,generateTemplate(request)));

        return "VERIFICATION MAIL WAS SENT AGAIN.";
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        log.info("CHECKING IF EMAIL EXISTS...");
        UserEntity userEntity = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(()->new CustomError("EMAIL DOES NOT EXIST","TRY WITH A DIFFERENT EMAIL"));
        log.info("IT EXISTS! NOW VERIFYING RESET REQUEST BY PUBLISHING EVENT");
        applicationEventPublisher.publishEvent(new ResetPasswordEvent(userEntity,resetPasswordRequest.getNewPassword(),generateTemplate(request)));
        return "RESET PASSWORD OPERATION IS COMPLETE.";
    }

    @Override
    public String saveResetToken(UserEntity userEntity, String resetPasswordToken, String newPassword) {
        log.info("CREATING RESET-PASSWORD ENTITY..");
        ResetPassword resetPassword = ResetPassword.builder()
                .resetToken(resetPasswordToken)
                .userEntity(userEntity)
                .newPassword(passwordEncoder.encode(newPassword))
                .expirationTime(Date.from(Instant.now().plusSeconds(AppConstant.EXPIRY_PERIOD)))
                .build();
        resetPasswordRepository.save(resetPassword);
        return "NEW PASSWORD SAVED IN DB.";
    }

    @Override
    public String resetConfirm(String resetToken) {
        log.info("CHECKING IF RESET-TOKEN EXISTS...");
        ResetPassword resetPassword = resetPasswordRepository.findByResetToken(resetToken).orElseThrow(()->new CustomError(AppConstant.TOKEN_DOES_NOT_EXIST,AppConstant.TRY_WITH_A_DIFFERENT_TOKEN));
        log.info("TOKEN EXISTS!, NOW CHECKING FOR TOKEN EXPIRY...");
        if(Date.from(Instant.now()).getTime() - resetPassword.getExpirationTime().getTime() >= 0){
            log.info("TOKEN EXPIRED. DELETING ENTRY FROM DB.");
            resetPasswordRepository.delete(resetPassword);
            return "TOKEN HAS EXPIRED.";
        }
        else{
            log.info("TOKEN IS ACTIVE! CHANGING PASSWORD...");
            UserEntity userEntity = resetPassword.getUserEntity();
            userEntity.setPassword(resetPassword.getNewPassword());
            userRepository.save(userEntity);
            resetPasswordRepository.delete(resetPassword);
            log.info("REMOVING PASSWORD RELATED ENTITY");
            return "PASSWORD RESET REQUEST HAS BEEN CONFIRMED.";
        }

    }

    private String generateTemplate(HttpServletRequest request) {
        log.info("GENERATING URL TEMPLATE...");
        return "http://"
                +
                request.getServerName()
                +
                ":"
                +
                request.getServerPort()
                +
                "/users";
    }
}
