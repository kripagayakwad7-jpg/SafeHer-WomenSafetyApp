package com.womensafety.repository;

import com.womensafety.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserId(Long userId);
    List<Contact> findByUserIdOrderByPriorityAsc(Long userId);
    List<Contact> findByContactEmailIgnoreCase(String contactEmail);
}
