
package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import in.kajalCoder.ContactSphere.services.EmailVerificationService;

@Controller
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(
            EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam(name = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean verified = emailVerificationService.verifyEmail(token);

        if (verified) {
            Message message = Message.builder()
                    .content("Email verified successfully! You can now log in.")
                    .type(MessageType.SUCCESS)
                    .build();

            redirectAttributes.addFlashAttribute("message", message);

            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Verification Failed");
        model.addAttribute(
                "error",
                "This verification link is invalid, expired, or already used.");

        return "email-verification-result";
    }
}
