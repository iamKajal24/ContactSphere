
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
import in.kajalCoder.ContactSphere.services.EmailVerificationService;
import in.kajalCoder.ContactSphere.services.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    public RegisterController(
            UserService userService,
            EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping
    public String register(Model model) {

        model.addAttribute("pageTitle", "Signup");

        UserFormRegisterRequest userFormRequest = new UserFormRegisterRequest();

        // Add an empty registration form to the model
        // userForm.setName("kajal");
        model.addAttribute("userFormRequest", userFormRequest);

        return "register";
    }

    // Processing register form

    @PostMapping("/do-register")
    public String processRegister(
            @Valid @ModelAttribute("userFormRequest") UserFormRegisterRequest userFormRequest,
            BindingResult rBindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Step 1: Check registration form validation errors
        if (rBindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Signup");
            return "register";
        }

        // Step 2: Normalize email by removing leading and trailing spaces
        String email = userFormRequest.getEmail();

        if (email != null) {
            email = email.trim();
            userFormRequest.setEmail(email);
        }

        // Step 3: Check whether the email is already registered
        if (email != null
                && !email.isBlank()
                && userService.isUserExistByEmail(email)) {

            // Add the duplicate email error to the email field
            rBindingResult.rejectValue(
                    "email",
                    "duplicate",
                    "This email is already registered. Please log in or use another email.");
        }

        // Step 4: Stop registration if duplicate email or another
        // validation error exists. Do not call saveUser().
        if (rBindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Signup");
            return "register";
        }

        // Step 5: Create a new user only after validation succeeds
        Users user = new Users();

        user.setName(userFormRequest.getName());
        user.setEmail(userFormRequest.getEmail());
        user.setPassword(userFormRequest.getPassword());
        user.setAbout(userFormRequest.getAbout());
        user.setPhoneNumber(userFormRequest.getPhoneNumber());
        user.setProfilePic("default.png");

        // Account remains inactive until email verification
        user.setEnabled(false);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setDeleted(false);
        user.setProvider(Provider.SELF);
        user.setProviderUserId(null);

        // Save user first
        // Password encoding is handled by UserServiceImplementation
        Users savedUsers = userService.saveUser(user);

        System.out.println("User saved with ID: "
                + savedUsers.getUserId());

        // Show registration success independently of email delivery
        Message message = Message.builder()
                .content("Registration successful!")
                .type(MessageType.SUCCESS)
                .build();

        redirectAttributes.addFlashAttribute("message", message);

        // Attempt to send verification email
        // Registration remains successful even if email delivery fails
        try {
            emailVerificationService.createAndSendVerificationToken(
                    savedUsers.getUserId(),
                    savedUsers.getEmail(),
                    savedUsers.getName());
        } catch (Exception e) {
            System.err.println(
                    "Verification email could not be sent: "
                            + e.getMessage());
        }

        // Redirect to registration page after successful registration
        return "redirect:/register";
    }

}
