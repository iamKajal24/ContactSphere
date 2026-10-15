
package in.kajalCoder.ContactSphere.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.kajalCoder.ContactSphere.dto.ContactFormRequest;
import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.helpers.Message;
import in.kajalCoder.ContactSphere.helpers.MessageType;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.CloudinaryService;
import in.kajalCoder.ContactSphere.services.ContactService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users/contact")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public ContactController(
            ContactService contactService,
            UserRepository userRepository,
            CloudinaryService cloudinaryService) {

        this.contactService = contactService;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // Open Add Contact page.
    @GetMapping("/add")
    public String addContactView(Model model) {

        model.addAttribute("pageTitle", "AddContact");
        model.addAttribute(
                "contactFormRequest",
                new ContactFormRequest());

        return "users/add_contact";
    }

    // Save contact details and optional contact photo.
    @PostMapping("/save-contact")
    public String saveContact(
            @Valid @ModelAttribute("contactFormRequest") ContactFormRequest form,

            BindingResult bindingResult,

            @RequestParam(name = "contactImage", required = false) MultipartFile contactImage,

            Authentication authentication,

            RedirectAttributes redirectAttributes,
            Model model) {

        // Step 1: Validate the form.
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "AddContact");
            return "users/add_contact";
        }

        // Step 2: Ensure the user is authenticated.
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            return "redirect:/login";
        }

        try {

            // Step 3: Extract the email from the logged-in principal.
            String email;
            Object principal = authentication.getPrincipal();

            if (principal instanceof OidcUser oidcUser) {

                // Google OpenID Connect login.
                email = oidcUser.getEmail();

            } else if (principal instanceof OAuth2User oauth2User) {

                // Other OAuth2 providers, if they expose an email.
                Object emailAttribute = oauth2User.getAttributes().get("email");

                email = emailAttribute != null
                        ? emailAttribute.toString()
                        : null;

            } else {

                // Normal email-password login.
                email = authentication.getName();
            }

            if (email == null || email.isBlank()) {
                logger.error(
                        "Email not available for authenticated principal: {}",
                        principal.getClass().getName());

                model.addAttribute("pageTitle", "AddContact");
                model.addAttribute(
                        "errorMessage",
                        "We could not identify your account. "
                                + "Please log in again.");

                model.addAttribute(
                        "contactFormRequest",
                        form);

                return "users/add_contact";
            }

            // Step 4: Find the existing user in the database.
            final String userEmail = email.trim();

            Users user = userRepository.findByEmail(userEmail)
                    .filter(existingUser -> !existingUser.isDeleted())
                    .orElse(null);

            if (user == null) {

                logger.warn(
                        "Authenticated account has no matching active database user. Email: {}",
                        userEmail);

                model.addAttribute("pageTitle", "AddContact");
                model.addAttribute(
                        "errorMessage",
                        "Your account was not found in our database. "
                                + "Please log out and log in again. "
                                + "If the problem continues, contact support.");

                model.addAttribute(
                        "contactFormRequest",
                        form);

                return "users/add_contact";
            }

            // Step 5: Create a contact and copy form data.
            Contact contact = new Contact();

            contact.setName(form.getName());
            contact.setEmail(form.getEmail());
            contact.setPhoneNumber(form.getPhoneNumber());
            contact.setAddress(form.getAddress());
            contact.setDescription(form.getDescription());
            contact.setFavorite(form.isFavorite());
            contact.setWebsiteLink(form.getWebsiteLink());
            contact.setLinkedInLink(form.getLinkedInLink());

            // Step 6: Upload the photo if one was selected.
            if (contactImage != null && !contactImage.isEmpty()) {

                // Enforce the 5 MB file-size limit on the server.
                if (contactImage.getSize() > 5 * 1024 * 1024) {

                    model.addAttribute("pageTitle", "AddContact");
                    model.addAttribute(
                            "errorMessage",
                            "Photo must be 5 MB or smaller.");

                    model.addAttribute(
                            "contactFormRequest",
                            form);

                    return "users/add_contact";
                }

                String imageUrl = cloudinaryService.uploadImage(contactImage);

                // Store the Cloudinary URL.
                contact.setPicture(imageUrl);
            }

            // Step 7: Associate the contact with its owner.
            contact.setUser(user);

            // Step 8: Save the contact in the database.
            contactService.save(contact);

            logger.info(
                    "Contact saved successfully for user ID: {}",
                    user.getUserId());

            Message message = Message.builder()
                    .content("Contact saved successfully")
                    .type(MessageType.SUCCESS)
                    .build();

            redirectAttributes.addFlashAttribute(
                    "message",
                    message);

            return "redirect:/users/contact/add";

        } catch (IOException e) {

            logger.error("Contact photo upload failed", e);

            model.addAttribute("pageTitle", "AddContact");
            model.addAttribute(
                    "errorMessage",
                    "Photo upload failed. Please try again.");

            model.addAttribute(
                    "contactFormRequest",
                    form);

            return "users/add_contact";
        }
    }
}