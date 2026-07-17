package com.womensafety.service;

import com.womensafety.dto.ContactRequest;
import com.womensafety.model.Contact;
import com.womensafety.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact addContact(ContactRequest request) {
        Contact contact = new Contact();
        contact.setUserId(request.getUserId());
        contact.setContactName(request.getContactName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setContactEmail(request.getContactEmail());
        contact.setPriority(contactRepository.findByUserId(request.getUserId()).size());
        return contactRepository.save(contact);
    }

    public List<Contact> getContacts(Long userId) {
        return contactRepository.findByUserIdOrderByPriorityAsc(userId);
    }

    public void reorderContacts(List<Long> orderedContactIds) {
        for (int i = 0; i < orderedContactIds.size(); i++) {
            Contact contact = contactRepository.findById(orderedContactIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Contact not found"));
            contact.setPriority(i);
            contactRepository.save(contact);
        }
    }

    public void deleteContact(Long contactId) {
        if (!contactRepository.existsById(contactId)) {
            throw new RuntimeException("Contact not found");
        }
        contactRepository.deleteById(contactId);
    }
}
