package com.socialshop.backend.authservice.services.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Table(name = "sessions")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private UserEntity user;


    @Column(nullable = false)
    @NotBlank
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isBlackList = false;

    @Column(nullable = false)
    private Instant expiredAt;

}
