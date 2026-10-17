
package in.kajalCoder.ContactSphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users/contact")
public class ViewAllContactsController {

    @GetMapping
    public String viewContact(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Forward the flash message to the next redirect.
        Object successMessage = model.getAttribute("successMessage");
        Object errorMessage = model.getAttribute("errorMessage");

        if (successMessage != null) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    successMessage);
        }

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    errorMessage);
        }

        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("filter", filter);
        redirectAttributes.addAttribute("page", Math.max(page, 0));

        return "redirect:/users/contact/search";
    }
}