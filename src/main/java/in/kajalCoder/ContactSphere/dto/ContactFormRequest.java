
package in.kajalCoder.ContactSphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactFormRequest {

    @NotBlank(message = "Contact name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @NotBlank(message = "Email address is required.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 254, message = "Email must not exceed 254 characters.")
    private String email;

    @Pattern(regexp = "^[+]?[0-9() .-]{7,20}$", message = "Please enter a valid phone number.")
    private String phoneNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters.")
    private String address;

    @Size(max = 5000, message = "Description must not exceed 5000 characters.")
    private String description;

    private boolean favorite;

    @Size(max = 2048, message = "Website URL must not exceed 2048 characters.")
    @Pattern(regexp = "^(https?://\\S+)?$", message = "Website must start with http:// or https://.")
    private String websiteLink;

    @Size(max = 2048, message = "LinkedIn URL must not exceed 2048 characters.")
    @Pattern(regexp = "^(https?://\\S+)?$", message = "LinkedIn URL must start with http:// or https://.")
    private String LinkedInLink;
}
