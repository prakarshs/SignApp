package com.Security.SignApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "RESET_PASSWORD_DETAILS")
public class ResetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resetId;

    private String resetToken;
    private String newPassword;

    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER", nullable = false, foreignKey = @ForeignKey(name = "FK_RESET_PASSWORD"))
    private UserEntity userEntity;
}
