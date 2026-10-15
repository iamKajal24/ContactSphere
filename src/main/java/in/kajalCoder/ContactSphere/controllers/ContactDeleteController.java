
package in.kajalCoder.ContactSphere.controllers;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.ContactService;

@Controller
public class ContactDeleteController {

    private final ContactService contactService;
    private final UserRepository userRepository;

    public ContactDeleteController(
            ContactService contactService,
            UserRepository userRepository) {
        this.contactService = contactService;
        this.userRepository = userRepository;
    }

    @PostMapping("/users/contact/{id}/delete")
    public String deleteContact(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();

        // Google OAuth2 login: authentication name may be Google's subject ID.
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            Object emailAttribute = attributes.get("email");

            if (emailAttribute instanceof String googleEmail
                    && !googleEmail.isBlank()) {
                email = googleEmail;
            }
        }

        Users currentUser = userRepository.findByEmail(email)
                .orElse(null);

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unable to identify your account. Please log in again.");
            return "redirect:/users/contact";
        }

        boolean deleted = contactService.deleteContact(
                id,
                currentUser.getUserId());

        // System.out.println("Contact ID: " + id);
        // System.out.println("Current User ID: " + currentUser.getUserId());
        // System.out.println("Contact deleted: " + deleted);
        // System.out.println("Adding flash message...");

        if (deleted) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    Message.builder()
                            .content("Contact deleted successfully.")
                            .type(MessageType.SUCCESS)
                            .build());
        } else {
            redirectAttributes.addFlashAttribute(
                    "message",
                    Message.builder()
                            .content("Contact not found or you are not allowed to delete it.")
                            .type(MessageType.ERROR)
                            .build());
        }

        System.out.println(
                "Flash message added: " +
                        redirectAttributes.getFlashAttributes());
        return "redirect:/users/contact";
    }
}
