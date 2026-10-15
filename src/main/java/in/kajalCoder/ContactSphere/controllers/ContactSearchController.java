
package in.kajalCoder.ContactSphere.controllers;

import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.ContactService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users/contact")
public class ContactSearchController {

        private static final int PAGE_SIZE = 6;

        private final ContactService contactService;
        private final UserRepository userRepository;

        public ContactSearchController(
                        ContactService contactService,
                        UserRepository userRepository) {
                this.contactService = contactService;
                this.userRepository = userRepository;
        }

        @GetMapping("/search")
        public String searchContacts(
                        Authentication authentication,
                        Model model,
                        @RequestParam(defaultValue = "") String keyword,
                        @RequestParam(defaultValue = "all") String filter,
                        @RequestParam(defaultValue = "0") int page) {

                // Require an authenticated user.
                if (authentication == null
                                || !authentication.isAuthenticated()
                                || "anonymousUser".equals(authentication.getName())) {
                        return "redirect:/login";
                }

                // Resolve the logged-in user.
                String email;

                if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                        email = oidcUser.getEmail();
                } else {
                        email = authentication.getName();
                }

                Users user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Logged-in user not found for email: " + email));

                Long userId = user.getUserId();

                // Normalize input.
                keyword = keyword.trim();

                boolean favoritesOnly = "favorites".equalsIgnoreCase(filter);
                filter = favoritesOnly ? "favorites" : "all";

                // Never allow negative page numbers.
                page = Math.max(page, 0);

                // Fetch only one page from the database.
                Pageable pageable = PageRequest.of(
                                page,
                                PAGE_SIZE,
                                Sort.by(Sort.Direction.ASC, "name"));

                Page<Contact> contactPage = contactService.searchContacts(
                                userId,
                                keyword,
                                favoritesOnly,
                                pageable);

                // If the requested page no longer exists, go back to page 1.
                if (contactPage.getTotalPages() > 0
                                && page >= contactPage.getTotalPages()) {

                        page = 0;
                        pageable = PageRequest.of(
                                        page,
                                        PAGE_SIZE,
                                        Sort.by(Sort.Direction.ASC, "name"));

                        contactPage = contactService.searchContacts(
                                        userId,
                                        keyword,
                                        favoritesOnly,
                                        pageable);
                }

                long totalContacts = contactService.countContacts(userId);
                long favoriteCount = contactService.countFavoriteContacts(userId);

                // Current page's contacts.
                List<Contact> contacts = contactPage.getContent();

                // Summary and pagination metadata for Thymeleaf.
                model.addAttribute("contacts", contacts);
                model.addAttribute("contactPage", contactPage);
                model.addAttribute("keyword", keyword);
                model.addAttribute("filter", filter);
                model.addAttribute("pageSize", PAGE_SIZE);

                model.addAttribute("totalContacts", totalContacts);
                model.addAttribute("favoriteCount", favoriteCount);
                model.addAttribute("searchResults", contactPage.getTotalElements());

                long pageStart = contactPage.getTotalElements() == 0
                                ? 0
                                : (long) contactPage.getNumber() * PAGE_SIZE + 1;

                long pageEnd = (long) contactPage.getNumber() * PAGE_SIZE
                                + contacts.size();

                model.addAttribute("pageStart", pageStart);
                model.addAttribute("pageEnd", pageEnd);

                return "users/contact";
        }
}
