
package in.kajalCoder.ContactSphere.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import in.kajalCoder.ContactSphere.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        String provider = oauthToken.getAuthorizedClientRegistrationId();

        if (!"google".equalsIgnoreCase(provider)) {
            response.sendRedirect(
                    "/login?oauth2Error=unsupportedProvider");
            return;
        }

        OidcUser googleUser = (OidcUser) authentication.getPrincipal();

        String email = googleUser.getEmail();
        String name = googleUser.getFullName();
        String googleSubject = googleUser.getSubject();
        String picture = googleUser.getPicture();

        System.out.println("Google login successful");
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Google subject received: "
                + (googleSubject != null && !googleSubject.isBlank()));

        // Save or verify Google user in database
        boolean saved = userService.processGoogleLogin(
                email, name, googleSubject, picture);

        if (saved) {
            System.out.println("Google user saved/verified successfully");
            response.sendRedirect("/users/profile");
        } else {
            System.out.println(
                    "Google user could not be saved or verified");
            response.sendRedirect(
                    "/login?oauth2Error=accountMismatch");
        }
    }
}