package in.kajalCoder.ContactSphere.services;

public interface EmailService {

    void sendVerificationEmail(
            String toEmail,
            String name,
            String verificationLink);

}
