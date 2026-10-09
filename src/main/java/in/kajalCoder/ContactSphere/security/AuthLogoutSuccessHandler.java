package in.kajalCoder.ContactSphere.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        HttpSession session = request.getSession();

        Message message = Message.builder()
                .content("You have been logged out successfully.")
                .type(MessageType.SUCCESS)
                .build();

        session.setAttribute("message", message);

        response.sendRedirect("/login");
    }

}
