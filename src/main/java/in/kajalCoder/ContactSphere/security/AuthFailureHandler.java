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

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        boolean accountDisabled = false;

        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof org.springframework.security.authentication.DisabledException) {
                accountDisabled = true;
                break;
            }

            cause = cause.getCause();
        }

        String errorMessage;

        if (accountDisabled) {
            errorMessage = "Please verify your email before logging in. Check your inbox for the verification link.";
        } else {
            errorMessage = "Invalid email or password.";
        }

        System.out.println("Login failure type: "
                + exception.getClass().getName());

        System.out.println("Login failure message: "
                + exception.getMessage());

        Message message = Message.builder()
                .content(errorMessage)
                .type(MessageType.ERROR)
                .build();

        request.getSession().setAttribute("message", message);

        response.sendRedirect("/login?error=true");
    }

}

// @Override
// public void onAuthenticationFailure(
// HttpServletRequest request,
// HttpServletResponse response,
// AuthenticationException exception)
// throws IOException, ServletException {

// // Message print
// Message message = Message.builder()
// .content("Invalid email or password.")
// .type(MessageType.ERROR)
// .build();

// HttpSession session = request.getSession();
// session.setAttribute("message", message);

// response.sendRedirect("/login?error=true");

// }
