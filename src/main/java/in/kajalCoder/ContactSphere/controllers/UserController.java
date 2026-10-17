
package in.kajalCoder.ContactSphere.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.ContactService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private final ContactService contactService;
    private final UserRepository userRepository;

    public UserController(
            ContactService contactService,
            UserRepository userRepository) {
        this.contactService = contactService;
        this.userRepository = userRepository;
    }

    // User dashboard
    @GetMapping
    public String dashboard(
            Authentication authentication,
            Model model,
            @SessionAttribute(value = "message", required = false) Message message,
            HttpSession session) {

        model.addAttribute("pageTitle", "Dashboard");

        // Display and clear the session message
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        // Check authentication
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        // Get the logged-in user's email
        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else if (principal instanceof OAuth2User oauth2User) {
            Object emailAttribute = oauth2User.getAttributes().get("email");
            if (emailAttribute != null) {
                email = emailAttribute.toString();
            }
        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        // Fetch the logged-in user from the database
        Users user = userRepository.findByEmail(email.trim())
                .filter(existingUser -> !existingUser.isDeleted())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Fetch dashboard statistics for this user only
        Long userId = user.getUserId();

        long totalContacts = contactService.countContacts(userId);
        long favoriteCount = contactService.countFavoriteContacts(userId);

        // Fetch the five most recently created contacts
        Page<Contact> recentPage = contactService.searchContacts(
                userId,
                "",
                false,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "id")));

        List<Contact> recentContacts = recentPage.getContent();

        // Send data to Thymeleaf
        model.addAttribute("currentUser", user);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteCount", favoriteCount);
        model.addAttribute("recentContacts", recentContacts);

        return "users/dashboard";
    }

    // Logged-in user's profile
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Profile");
        return "users/profile";
    }
}
