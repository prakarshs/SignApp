package com.Security.SignApp.Events;

import com.Security.SignApp.Entity.UserEntity;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class UserActivationEvent extends ApplicationEvent {
    private String urlTemplate;
    private UserEntity user;
    public UserActivationEvent(UserEntity user, String urlTemplate){
        super(user);
        this.user = user;
        this.urlTemplate = urlTemplate;
    }
}
