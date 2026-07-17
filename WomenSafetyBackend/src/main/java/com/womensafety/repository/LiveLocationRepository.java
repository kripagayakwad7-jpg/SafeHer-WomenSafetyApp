package com.womensafety.repository;

import com.womensafety.model.LiveLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveLocationRepository extends JpaRepository<LiveLocation, Long> {
}
