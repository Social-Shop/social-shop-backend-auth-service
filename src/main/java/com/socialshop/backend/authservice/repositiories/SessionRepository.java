package com.socialshop.backend.authservice.repositiories;

import com.socialshop.backend.authservice.services.model.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,Long> {

    SessionEntity findByRefreshToken(String refreshToken);
}
