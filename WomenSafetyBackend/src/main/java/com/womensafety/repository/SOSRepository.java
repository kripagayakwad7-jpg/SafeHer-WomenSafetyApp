package com.womensafety.repository;

import com.womensafety.model.SOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SOSRepository extends JpaRepository<SOS, Long> {
    List<SOS> findByUserIdOrderByCreatedAtDesc(Long userId);
}
