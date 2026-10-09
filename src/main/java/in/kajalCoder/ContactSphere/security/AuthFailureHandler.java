package in.kajalCoder.ContactSphere.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        // Message print
        Message message = Message.builder()
                .content("Invalid email or password.")
                .type(MessageType.ERROR)
                .build();

        HttpSession session = request.getSession();
        session.setAttribute("message", message);

        response.sendRedirect("/login?error=true");

    }

}
