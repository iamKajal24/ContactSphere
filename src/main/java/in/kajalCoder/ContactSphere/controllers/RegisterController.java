package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.kajalCoder.ContactSphere.dto.UserFormRegisterRequest;
import in.kajalCoder.ContactSphere.entity.Provider;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import in.kajalCoder.ContactSphere.services.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String register(Model model) {
        model.addAttribute("pageTitle", "Signup");
        UserFormRegisterRequest userFormRequest = new UserFormRegisterRequest();
        // userForm.setName("kajal");
        model.addAttribute("userFormRequest", userFormRequest);
        return "register";
    }

    // Processing register form

    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute("userFormRequest") UserFormRegisterRequest userFormRequest,
            BindingResult rBindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        // fetch data
        // System.out.println(userFormRequest);

        // validatation form data
        if (rBindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Signup");
            return "register";
        }

        Users user = new Users();

        user.setName(userFormRequest.getName());
        user.setEmail(userFormRequest.getEmail());
        user.setPassword(userFormRequest.getPassword());
        user.setAbout(userFormRequest.getAbout());
        user.setPhoneNumber(userFormRequest.getPhoneNumber());
        user.setProfilePic("default.png");

        // Default values

        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setDeleted(false);
        user.setProvider(Provider.SELF);

        // saved into database;

        Users savedUsers = userService.saveUser(user);

        System.out.println("User Saved : " + savedUsers);

        // Message

        Message message = Message.builder()
                .content("Registration successful!")
                .type(MessageType.SUCCESS)
                .build();

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/register";
    }

}
