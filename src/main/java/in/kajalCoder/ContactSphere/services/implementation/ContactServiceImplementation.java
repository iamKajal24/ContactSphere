package in.kajalCoder.ContactSphere.services.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import in.kajalCoder.ContactSphere.entity.Contact;
import in.kajalCoder.ContactSphere.exceptions.ResourceNotFoundExceptions;
import in.kajalCoder.ContactSphere.repository.ContactRepository;
import in.kajalCoder.ContactSphere.services.ContactService;

@Service
public class ContactServiceImplementation implements ContactService {

    private ContactRepository contactRepository;

    public ContactServiceImplementation(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
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

}
