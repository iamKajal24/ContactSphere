package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import in.kajalCoder.ContactSphere.helpers.Message;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    // user dashboad page
    @GetMapping
    public String dashboard(
            Model model,
            @SessionAttribute(value = "message", required = false) Message message,
            HttpSession session) {

        // for message
        if (message != null) {

            System.out.println("===== MESSAGE FROM SESSION: " + message);
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "users/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "users/profile";
    }

}
