
package in.kajalCoder.ContactSphere.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.kajalCoder.ContactSphere.dto.FeedbackForm;
import in.kajalCoder.ContactSphere.entity.FeedbackCategory;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.FeedbackService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    public FeedbackController(
            FeedbackService feedbackService,
            UserRepository userRepository) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showFeedbackForm(Model model) {

        if (!model.containsAttribute("feedbackForm")) {
            model.addAttribute("feedbackForm", new FeedbackForm());
        }

        model.addAttribute("categories", FeedbackCategory.values());

        return "users/feedback";
    }

    @PostMapping
    public String submitFeedback(
            @Valid @ModelAttribute("feedbackForm") FeedbackForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", FeedbackCategory.values());
            return "users/feedback";
        }

        String email = getAuthenticatedEmail(authentication);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user was not found in the database"));

        feedbackService.submitFeedback(form, user);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Thank you! Your feedback has been submitted successfully.");

        return "redirect:/users/feedback";
    }

    @GetMapping("/my")
    public String showMyFeedback(
            Authentication authentication,
            Model model) {

        String email = getAuthenticatedEmail(authentication);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user was not found in the database"));

        model.addAttribute(
                "feedbackList",
                feedbackService.getMyFeedback(user.getUserId()));

        return "users/my-feedback";
    }

    private String getAuthenticatedEmail(Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object email = oauth2User.getAttribute("email");

            if (email instanceof String emailValue && !emailValue.isBlank()) {
                return emailValue;
            }
        }

        return authentication.getName();
    }
}
