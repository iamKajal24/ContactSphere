
package in.kajalCoder.ContactSphere.services.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import in.kajalCoder.ContactSphere.services.EmailService;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImplementation implements EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  public EmailServiceImplementation(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendVerificationEmail(
      String toEmail,
      String name,
      String verificationLink) {

    System.out.println("[EMAIL] Sending HTML verification email...");
    System.out.println("[EMAIL] Recipient: " + toEmail);

    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(
          mimeMessage,
          MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          "UTF-8");

      String safeName = escapeHtml(name);
      String safeLink = escapeHtml(verificationLink);

      String html = """
          <!DOCTYPE html>
          <html lang="en">
          <body style="margin:0;padding:0;background:#f3f4f6;
                       font-family:Arial,sans-serif;color:#374151;">

            <div style="max-width:600px;margin:30px auto;
                        background:#ffffff;border-radius:12px;
                        overflow:hidden;border:1px solid #e5e7eb;">

              <div style="background:#2563eb;color:#ffffff;
                          padding:28px;text-align:center;">
                <h1 style="margin:0;">ContactSphere</h1>
                <p style="margin:10px 0 0;">
                  Your contacts, organized.
                </p>
              </div>

              <div style="padding:32px;">
                <h2 style="color:#111827;">
                  Verify your email
                </h2>

                <p>Hello %s,</p>

                <p>
                  Thank you for registering with ContactSphere.
                  Verify your email address to activate your account.
                </p>

                <div style="text-align:center;margin:30px 0;">
                  <a href="%s"
                     style="background:#2563eb;color:#ffffff;
                            padding:14px 24px;
                            text-decoration:none;
                            border-radius:8px;
                            display:inline-block;
                            font-weight:bold;">
                    Verify My Email
                  </a>
                </div>

                <p>
                  This link expires in 24 hours and can only
                  be used once.
                </p>

                <p style="font-size:13px;color:#6b7280;">
                  If the button does not work, open this link:
                </p>

                <p style="font-size:12px;overflow-wrap:anywhere;">
                  <a href="%s">%s</a>
                </p>

                <p>
                  If you did not create this account,
                  you can ignore this email.
                </p>

                <p>Regards,<br>ContactSphere Team</p>
              </div>
            </div>
          </body>
          </html>
          """.formatted(
          safeName, safeLink, safeLink, safeLink);

      String plainText = """
          Hello %s,

          Please verify your ContactSphere email using this link:
          %s

          This link expires in 24 hours and can only be used once.

          Regards,
          ContactSphere Team
          """.formatted(name, verificationLink);

      helper.setFrom(fromEmail);
      helper.setTo(toEmail);
      helper.setSubject("Verify your ContactSphere email");

      // First argument: plain-text fallback
      // Second argument: HTML email
      helper.setText(plainText, html);

      mailSender.send(mimeMessage);

      System.out.println("[EMAIL] HTML email sent successfully.");

    } catch (Exception e) {
      System.err.println("[EMAIL] Sending failed: "
          + e.getClass().getName());
      System.err.println("[EMAIL] Error message: "
          + e.getMessage());

      throw new RuntimeException(
          "Could not send verification email", e);
    }
  }

  private String escapeHtml(String value) {
    if (value == null) {
      return "";
    }

    return value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
