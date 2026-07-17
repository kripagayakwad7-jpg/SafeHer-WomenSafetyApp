package com.womensafety.service;

import com.womensafety.dto.LocationUpdateRequest;
import com.womensafety.dto.WardLocationResponse;
import com.womensafety.model.Contact;
import com.womensafety.model.LiveLocation;
import com.womensafety.model.User;
import com.womensafety.repository.ContactRepository;
import com.womensafety.repository.LiveLocationRepository;
import com.womensafety.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LiveLocationRepository liveLocationRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public LocationService(LiveLocationRepository liveLocationRepository,
                            ContactRepository contactRepository,
                            UserRepository userRepository) {
        this.liveLocationRepository = liveLocationRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public void updateLocation(LocationUpdateRequest request) {
        LiveLocation location = liveLocationRepository.findById(request.getUserId())
                .orElse(new LiveLocation());
        location.setUserId(request.getUserId());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setSharing(request.getSharing());
        location.setUpdatedAt(LocalDateTime.now());
        liveLocationRepository.save(location);
    }

    /**
     * Finds every person who has registered this guardian's email as a trusted
     * contact, and returns each one's latest shared location (if any).
     */
    public List<WardLocationResponse> getWardsForGuardian(String guardianEmail) {
        List<Contact> contactsListingThisGuardian = contactRepository.findByContactEmailIgnoreCase(guardianEmail);
        List<WardLocationResponse> result = new ArrayList<>();

        for (Contact contact : contactsListingThisGuardian) {
            Optional<User> wardUser = userRepository.findById(contact.getUserId());
            if (wardUser.isEmpty()) continue;

            User ward = wardUser.get();
            Optional<LiveLocation> location = liveLocationRepository.findById(ward.getUserId());

            if (location.isPresent()) {
                LiveLocation loc = location.get();
                result.add(new WardLocationResponse(
                        ward.getUserId(), ward.getName(), ward.getPhone(),
                        loc.getLatitude(), loc.getLongitude(), loc.isSharing(), loc.getUpdatedAt()
                ));
            } else {
                result.add(new WardLocationResponse(
                        ward.getUserId(), ward.getName(), ward.getPhone(),
                        null, null, false, null
                ));
            }
        }
        return result;
    }
}
