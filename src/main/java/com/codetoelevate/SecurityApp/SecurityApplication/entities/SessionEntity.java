package com.codetoelevate.SecurityApp.SecurityApplication.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SessionEntity {

    @Id
    private Long userId;

    @Column(nullable = false, length = 2048)
    private String refreshToken;

    @CreationTimestamp
    private Date createdAt;

    public SessionEntity(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
