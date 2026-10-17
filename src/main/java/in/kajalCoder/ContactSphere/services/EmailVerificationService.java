package in.kajalCoder.ContactSphere.services;

public interface EmailVerificationService {

    void createAndSendVerificationToken(
            Long userId,
            String email,
            String name);

    boolean verifyEmail(String token);

}

// for email token generate