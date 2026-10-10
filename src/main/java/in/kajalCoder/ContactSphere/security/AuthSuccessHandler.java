package in.kajalCoder.ContactSphere.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        System.out.println("===== AUTH SUCCESS HANDLER CALLED =====");

        Message message = Message.builder()
                .content("Login successful! Welcome to ContactSphere.")
                .type(MessageType.SUCCESS)
                .build();

        HttpSession session = request.getSession();
        session.setAttribute("message", message);

        response.sendRedirect("/users/profile");

    }

}
