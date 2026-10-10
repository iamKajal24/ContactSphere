package in.kajalCoder.ContactSphere.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.services.UserService;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;

    public GlobalModelAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addCurrentUserToModel(
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else if (principal instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
        } else {
            // Email/password login: username email hona chahiye
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return;
        }

        Users user = userService.getUserByEmail(email);

        if (user != null && !user.isDeleted() && user.isEnabled()) {
            model.addAttribute("currentUser", user);
        }
    }

}