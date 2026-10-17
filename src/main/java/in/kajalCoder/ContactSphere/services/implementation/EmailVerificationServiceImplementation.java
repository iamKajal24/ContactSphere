
package in.kajalCoder.ContactSphere.services.implementation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.kajalCoder.ContactSphere.entity.EmailVerificationToken;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.EmailVerificationTokenRepository;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.EmailService;
import in.kajalCoder.ContactSphere.services.EmailVerificationService;

@Service
public class EmailVerificationServiceImplementation
        implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailVerificationServiceImplementation(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void createAndSendVerificationToken(
            Long userId,
            String email,
            String name) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Remove previously issued verification tokens
        tokenRepository.deleteByUser_UserId(userId);

        // Generate a cryptographically secure random token
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Store only the SHA-256 hash in the database
        EmailVerificationToken verificationToken = new EmailVerificationToken();

        verificationToken.setTokenHash(hashToken(rawToken));
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(
                LocalDateTime.now().plusHours(24));

        tokenRepository.save(verificationToken);

        String verificationLink = baseUrl.replaceAll("/+$", "")
                + "/verify-email?token=" + rawToken;

        // Send the original token only through the verification link
        emailService.sendVerificationEmail(
                email, name, verificationLink);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }

        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken = tokenRepository.findByTokenHash(tokenHash)
                .orElse(null);

        if (verificationToken == null) {
            return false;
        }

        if (verificationToken.getUsedAt() != null) {
            return false;
        }

        if (!verificationToken.getExpiresAt()
                .isAfter(LocalDateTime.now())) {
            return false;
        }

        Users user = verificationToken.getUser();

        if (user.isDeleted()) {
            return false;
        }

        user.setEmailVerified(true);
        user.setEnabled(true);

        verificationToken.setUsedAt(LocalDateTime.now());

        userRepository.save(user);
        tokenRepository.save(verificationToken);

        return true;
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable", exception);
        }
    }
}
