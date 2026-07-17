package com.womensafety.controller;

import com.womensafety.dto.ApiResponse;
import com.womensafety.dto.SOSRequest;
import com.womensafety.model.SOS;
import com.womensafety.service.SOSService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.womensafety.dto.SOSStatusUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
public class SOSController {

    private final SOSService sosService;

    public SOSController(SOSService sosService) {
        this.sosService = sosService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> triggerSOS(@Valid @RequestBody SOSRequest request) {
        try {
            SOS sos = sosService.recordSOS(request);
            return ResponseEntity.ok(new ApiResponse(true, "SOS recorded successfully", sos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse> getHistory(@PathVariable Long userId) {
        try {
            List<SOS> history = sosService.getHistory(userId);
            return ResponseEntity.ok(new ApiResponse(true, "SOS history fetched", history));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{sosId}/status")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long sosId, @RequestBody SOSStatusUpdateRequest request) {
        try {
            SOS sos = sosService.updateStatus(sosId, request.getStatus());
            return ResponseEntity.ok(new ApiResponse(true, "Status updated", sos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

}
