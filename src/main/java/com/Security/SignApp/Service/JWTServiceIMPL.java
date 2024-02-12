package com.Security.SignApp.Service;

import com.Security.SignApp.Constant.AppConstant;
import com.Security.SignApp.Entity.UserEntity;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Service
@Log4j2
public class JWTServiceIMPL implements JWTService{
    @Override
    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(AppConstant.EXPIRY_PERIOD)))
                .subject(user.getEmail())
                .claim("fullName",user.getFullName())
                .claim("role",user.getRole())
                .signWith(Jwts.SIG.HS256.key().build())
                .compact();

    }
}
