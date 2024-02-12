package com.Security.SignApp.Service;

import com.Security.SignApp.Entity.UserEntity;

public interface JWTService {
   String generateToken(UserEntity user);
}
