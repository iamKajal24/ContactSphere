
package in.kajalCoder.ContactSphere.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.kajalCoder.ContactSphere.dto.ContactResponse;
import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.ContactRepository;
import in.kajalCoder.ContactSphere.repository.UserRepository;

@RestController
@RequestMapping("/api/contacts")

public class ContactApiController {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public ContactApiController(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContact(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to view this contact."));
        }

        String email = getLoggedInEmail(authentication);

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unable to identify the logged-in user."));
        }

        Users loggedInUser = userRepository.findByEmail(email).orElse(null);

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Logged-in user was not found."));
        }

        Contact contact = contactRepository.findById(id).orElse(null);

        // Return the same response if the contact doesn't exist
        // or belongs to another user.
        if (contact == null
                || contact.getUser() == null
                || !contact.getUser().getUserId()
                        .equals(loggedInUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Contact not found."));
        }

        return ResponseEntity.ok(ContactResponse.from(contact));
    }

    private String getLoggedInEmail(Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        }

        if (principal instanceof OAuth2User oauth2User) {
            return oauth2User.getAttribute("email");
        }

        // For regular email/password login.
        return authentication.getName();
    }
}
