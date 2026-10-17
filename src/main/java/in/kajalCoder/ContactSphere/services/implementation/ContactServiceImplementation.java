package in.kajalCoder.ContactSphere.services.implementation;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.kajalCoder.ContactSphere.dto.ContactFormRequest;
import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.exceptions.ResourceNotFoundExceptions;
import in.kajalCoder.ContactSphere.repository.ContactRepository;
import in.kajalCoder.ContactSphere.services.CloudinaryService;
import in.kajalCoder.ContactSphere.services.ContactService;

@Service
public class ContactServiceImplementation implements ContactService {

    private ContactRepository contactRepository;

    private final CloudinaryService cloudinaryService;

    public ContactServiceImplementation(
            ContactRepository contactRepository,
            CloudinaryService cloudinaryService) {

        this.contactRepository = contactRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact contact) {

        Contact existingContact = contactRepository.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "Contact not found with id : " + contact.getId()));

        existingContact.setName(contact.getName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setPhoneNumber(contact.getPhoneNumber());
        existingContact.setAddress(contact.getAddress());
        existingContact.setPicture(contact.getPicture());
        existingContact.setDescription(contact.getDescription());
        existingContact.setFavorite(contact.isFavorite());
        existingContact.setWebsiteLink(contact.getWebsiteLink());
        existingContact.setLinkedInLink(contact.getLinkedInLink());

        return contactRepository.save(existingContact);

    }

    @Override
    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "Contact not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundExceptions(
                    "Contact not found with id: " + id);
        }

        contactRepository.deleteById(id);
    }

    @Override
    public List<Contact> getByUserId(Long userId) {
        return contactRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Contact> getFavoritesByUserId(Long userId) {
        return contactRepository.findByUser_UserIdAndFavoriteTrue(userId);
    }

    @Override
    public Page<Contact> searchContacts(Long userId, String keyword, boolean favoritesOnly, Pageable pageable) {
        return contactRepository.searchContacts(userId,
                keyword,
                favoritesOnly,
                pageable);
    }

    @Override
    public long countContacts(Long userId) {
        return contactRepository.countByUser_UserId(userId);
    }

    @Override
    public long countFavoriteContacts(Long userId) {
        return contactRepository.countByUser_UserIdAndFavoriteTrue(userId);
    }

    @Override
    public boolean deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepository
                .findByIdAndUser_UserId(contactId, userId)
                .orElse(null);

        if (contact == null) {
            return false;
        }

        contactRepository.delete(contact);
        return true;
    }

    @Override
    public Contact updateContact(Long contactId, Long userId, ContactFormRequest form, MultipartFile contactImage) {
        // Fetch contact only if it belongs to this user
        Contact existingContact = contactRepository
                .findByIdAndUser_UserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "Contact not found with id: " + contactId));

        // Upload a new picture only if selected
        if (contactImage != null && !contactImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(contactImage);
                existingContact.setPicture(imageUrl);

            } catch (IOException | IllegalArgumentException ex) {
                throw new IllegalStateException(
                        "Unable to upload contact picture.", ex);
            }
        }

        // Update contact fields
        existingContact.setName(form.getName());
        existingContact.setEmail(form.getEmail());
        existingContact.setPhoneNumber(form.getPhoneNumber());
        existingContact.setAddress(form.getAddress());
        existingContact.setDescription(form.getDescription());
        existingContact.setFavorite(form.isFavorite());
        existingContact.setWebsiteLink(form.getWebsiteLink());
        existingContact.setLinkedInLink(form.getLinkedInLink());

        return contactRepository.save(existingContact);
    }

}
