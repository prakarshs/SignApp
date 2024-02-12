package com.Security.SignApp.Events;

import com.Security.SignApp.Entity.UserEntity;
import com.Security.SignApp.Model.MailTemplate;
import com.Security.SignApp.Service.JWTService;
import com.Security.SignApp.Service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
public class UserActivationListener implements ApplicationListener<UserActivationEvent> {
    @Autowired
    private UserService userService;


    @Override
    public void onApplicationEvent(UserActivationEvent event) {
        UserEntity user = event.getUser();
        String urlTemplate = event.getUrlTemplate();


        String verificationToken = UUID.randomUUID().toString();
        log.info(userService.saveToken(user, verificationToken));

        String verificationLink = urlTemplate + "/activate?token=" + verificationToken;

        log.info("CREATING MAIL CONTENT...");
        MailTemplate mailTemplate = MailTemplate.builder()
                .subject("SignApp Verification Mail")
                .content("Dear " + user.getFullName() + ",\n" +
                        "\n" +
                        "Thank you for signing up with SignApp! To activate your account and gain full access to our services, please click the link below:\n\n"
                        +
                        verificationLink)
                .build();

          log.info(userService.sendMail(mailTemplate, user.getEmail()));

          System.out.println(verificationLink);
    }

}
