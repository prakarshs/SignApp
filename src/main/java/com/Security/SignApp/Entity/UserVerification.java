package com.Security.SignApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.cdi.Eager;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "USER_VERIFICATION_DETAILS")
public class UserVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    private Date expirationTime;
    private String verificationToken;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_TOKEN"))
    private UserEntity user;
}
