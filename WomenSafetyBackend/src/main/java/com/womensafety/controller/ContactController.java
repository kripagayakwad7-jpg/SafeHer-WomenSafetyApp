package com.womensafety.controller;

import com.womensafety.dto.ApiResponse;
import com.womensafety.dto.ContactRequest;
import com.womensafety.model.Contact;
import com.womensafety.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.womensafety.dto.ReorderContactsRequest;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addContact(@Valid @RequestBody ContactRequest request) {
        try {
            Contact contact = contactService.addContact(request);
            return ResponseEntity.ok(new ApiResponse(true, "Contact added", contact));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse> reorderContacts(@RequestBody ReorderContactsRequest request) {
        try {
            contactService.reorderContacts(request.getContactIds());
            return ResponseEntity.ok(new ApiResponse(true, "Contacts reordered", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getContacts(@PathVariable Long userId) {
        try {
            List<Contact> contacts = contactService.getContacts(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Contacts fetched", contacts));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse> deleteContact(@PathVariable Long contactId) {
        try {
            contactService.deleteContact(contactId);
            return ResponseEntity.ok(new ApiResponse(true, "Contact deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
