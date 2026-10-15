
package in.kajalCoder.ContactSphere.dto;

import java.time.LocalDateTime;

import in.kajalCoder.ContactSphere.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String picture;
    private String description;
    private boolean favorite;
    private String websiteLink;
    private String linkedInLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContactResponse from(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getPhoneNumber(),
                contact.getAddress(),
                contact.getPicture(),
                contact.getDescription(),
                contact.isFavorite(),
                contact.getWebsiteLink(),
                contact.getLinkedInLink(),
                contact.getCreatedAt(),
                contact.getUpdatedAt());
    }
}
