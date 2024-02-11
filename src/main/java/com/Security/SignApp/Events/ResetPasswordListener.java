package com.Security.SignApp.Events;

import com.Security.SignApp.Entity.UserEntity;
import com.Security.SignApp.Model.MailTemplate;
import com.Security.SignApp.Service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
public class ResetPasswordListener implements ApplicationListener<ResetPasswordEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {
        UserEntity userEntity = event.getUser();
        String templateUrl = event.getTemplateUrl();
        String newPassword = event.getNewPassword();
        String resetPasswordToken = UUID.randomUUID().toString();

        log.info(userService.saveResetToken(userEntity, resetPasswordToken, newPassword));

        String resetLink = templateUrl + "/reset?token=" + resetPasswordToken;

        log.info("CREATING MAIL CONTENT...");
        MailTemplate mailTemplate = MailTemplate.builder()
                .subject("SignApp Resend Password Mail")
                .content("Dear " + userEntity.getFullName() + ",\n" +
                        "\n" +
                        "You are receiving this email because a password reset request was initiated for your account.\nTo reset your password, please click the link below:\n\n"
                        +
                        resetLink
                        + "\n\nIf you did not request a password reset, you can safely ignore this email. Your password will not be changed unless you confirm the reset request by clicking the link above.\n")
                .build();
        log.info(userService.sendMail(mailTemplate, userEntity.getEmail()));
        System.out.println(resetLink);
    }
}
