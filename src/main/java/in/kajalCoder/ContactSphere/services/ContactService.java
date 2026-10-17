package in.kajalCoder.ContactSphere.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.kajalCoder.ContactSphere.dto.ContactFormRequest;
import in.kajalCoder.ContactSphere.entity.Contact;

public interface ContactService {

    // save
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    Contact updateContact(
            Long contactId,
            Long userId,
            ContactFormRequest form,
            MultipartFile contactImage);

    // get contacts
    List<Contact> getAll();

    // get contact by id

    Contact getById(Long id);

    // delete contact

    void delete(Long id);

    // for view all contact list
    List<Contact> getByUserId(Long userId);

    // Sirf favorite contacts fetch karne ke liye
    List<Contact> getFavoritesByUserId(Long userId);

    // for search
    Page<Contact> searchContacts(
            Long userId,
            String keyword,
            boolean favoritesOnly,
            Pageable pageable);

    long countContacts(Long userId);

    long countFavoriteContacts(Long userId);

    // for contact delete method
    boolean deleteContact(Long contactId, Long userId);

}
