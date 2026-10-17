package in.kajalCoder.ContactSphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kajalCoder.ContactSphere.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends
        JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    void deleteByUser_UserId(Long userId);
}
