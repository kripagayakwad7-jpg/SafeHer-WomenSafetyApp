package com.womensafety.dto;

import java.time.LocalDateTime;

/**
 * Represents one person a guardian is protecting: who they are, and their
 * most recently shared location (if they currently have sharing turned on).
 */
public class WardLocationResponse {

    private Long userId;
    private String name;
    private String phone;
    private Double latitude;
    private Double longitude;
    private boolean sharing;
    private LocalDateTime updatedAt;

    public WardLocationResponse() {}

    public WardLocationResponse(Long userId, String name, String phone, Double latitude,
                                 Double longitude, boolean sharing, LocalDateTime updatedAt) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sharing = sharing;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public boolean isSharing() { return sharing; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
