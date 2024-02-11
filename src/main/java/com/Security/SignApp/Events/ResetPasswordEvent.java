package com.Security.SignApp.Events;

import com.Security.SignApp.Entity.UserEntity;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class ResetPasswordEvent extends ApplicationEvent {
    private UserEntity user;
    private String newPassword;
    private String templateUrl;
    public ResetPasswordEvent(UserEntity user, String newPassword, String templateUrl){
        super(user);
        this.user = user;
        this.newPassword = newPassword;
        this.templateUrl = templateUrl;

    }
}
