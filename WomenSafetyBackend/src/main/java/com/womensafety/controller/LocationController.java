package com.womensafety.controller;

import com.womensafety.dto.ApiResponse;
import com.womensafety.dto.LocationUpdateRequest;
import com.womensafety.dto.WardLocationResponse;
import com.womensafety.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Called repeatedly by the "sharing" user's app (e.g. every 10-15s) to
     * update their current position, or to turn sharing off.
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        try {
            locationService.updateLocation(request);
            return ResponseEntity.ok(new ApiResponse(true, "Location updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Called by a guardian's app to see everyone who has listed them as a
     * trusted contact, along with each person's latest shared location.
     */
    @GetMapping("/wards")
    public ResponseEntity<ApiResponse> getWards(@RequestParam String guardianEmail) {
        try {
            List<WardLocationResponse> wards = locationService.getWardsForGuardian(guardianEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Wards fetched", wards));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
