package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import in.kajalCoder.ContactSphere.helpers.Message;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login(Model model,
            @SessionAttribute(value = "message", required = false) Message message,
            HttpSession session) {

        model.addAttribute("pageTitle", "Login");

        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "login";
    }

}
