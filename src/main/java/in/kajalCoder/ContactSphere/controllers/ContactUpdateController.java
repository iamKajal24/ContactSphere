package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import in.kajalCoder.ContactSphere.dto.ContactFormRequest;
import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.ContactRepository;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.ContactService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/users/contact")
public class ContactUpdateController {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ContactService contactService;

    public ContactUpdateController(
            ContactRepository contactRepository,
            UserRepository userRepository,
            ContactService contactService) {

        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.contactService = contactService;
    }

    @GetMapping("/{contactId}/edit")
    public String showEditForm(
            @PathVariable Long contactId,
            Authentication authentication,
            Model model) {

        model.addAttribute("pageTitle", "Edit Contact");

        // Step 1: Check whether the user is logged in
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            return "redirect:/login";
        }

        // Step 2: Get email of the logged-in user
        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();

        } else if (principal instanceof OAuth2User oauth2User) {
            Object emailAttribute = oauth2User.getAttributes().get("email");
            email = emailAttribute != null
                    ? emailAttribute.toString()
                    : null;

        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        // Step 3: Find the logged-in user in the database
        Users user = userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.isDeleted())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Step 4: Fetch only this user's contact
        Contact contact = contactRepository
                .findByIdAndUser_UserId(contactId, user.getUserId())
                .orElse(null);

        if (contact == null) {
            return "redirect:/users/contact";
        }

        // Temporary: pass the fetched contact to the view
        model.addAttribute("contact", contact);
        model.addAttribute("contactId", contact.getId());
        model.addAttribute("currentPicture", contact.getPicture());

        ContactFormRequest form = new ContactFormRequest();

        form.setName(contact.getName());
        form.setEmail(contact.getEmail());
        form.setPhoneNumber(contact.getPhoneNumber());
        form.setAddress(contact.getAddress());
        form.setDescription(contact.getDescription());
        form.setFavorite(contact.isFavorite());
        form.setWebsiteLink(contact.getWebsiteLink());
        form.setLinkedInLink(contact.getLinkedInLink());

        model.addAttribute("contactFormRequest", form);

        return "users/edit_contact";

    }

    @PostMapping("/{contactId}/update")
    public String updateContact(
            @PathVariable Long contactId,
            @Valid @ModelAttribute("contactFormRequest") ContactFormRequest form,
            BindingResult bindingResult,
            @RequestParam(value = "contactImage", required = false) MultipartFile contactImage,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("pageTitle", "Edit Contact");

        // 1. Check login
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            return "redirect:/login";
        }

        // 2. Get logged-in user's email
        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();

        } else if (principal instanceof OAuth2User oauth2User) {
            Object emailAttribute = oauth2User.getAttributes().get("email");
            email = emailAttribute != null
                    ? emailAttribute.toString()
                    : null;

        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        // 3. Find active user
        Users user = userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.isDeleted())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // 4. Fetch only this user's contact
        Contact contact = contactRepository
                .findByIdAndUser_UserId(contactId, user.getUserId())
                .orElse(null);

        if (contact == null) {
            return "redirect:/users/contact";
        }

        // 5. Keep existing image available for the form
        model.addAttribute("contactId", contact.getId());
        model.addAttribute("currentPicture", contact.getPicture());

        // 6. If validation fails, return to the edit form
        if (bindingResult.hasErrors()) {
            return "users/edit_contact";
        }

        try {
            contactService.updateContact(
                    contactId,
                    user.getUserId(),
                    form,
                    contactImage);
        } catch (IllegalStateException ex) {

            model.addAttribute(
                    "imageError",
                    "Photo upload failed. Please try another image.");

            return "users/edit_contact";

        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Contact updated successFully!");

        return "redirect:/users/contact";
    }

}
