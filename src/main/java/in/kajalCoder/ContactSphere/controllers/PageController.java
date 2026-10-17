package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping({ "/", "/home" })
    public String Home(Model model) {
        System.out.println("Home page controller");

        // for page title
        model.addAttribute("pageTitle", "Home");

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        System.out.println("about page controller");

        // for page Title
        model.addAttribute("pageTitle", "About");
        return "about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        System.out.println("service page controller");

        // for page title
        model.addAttribute("pageTitle", "Services");
        return "service";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        System.out.println("contact page controller");

        // for page title
        model.addAttribute("pageTitle", "Contact");
        return "contact";
    }

}
