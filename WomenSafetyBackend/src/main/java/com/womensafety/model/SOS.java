package com.womensafety.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "sos_history")
public class SOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sos_id")
    private Long sosId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "sos_date", nullable = false)
    private LocalDate sosDate;

    @Column(name = "sos_time", nullable = false)
    private LocalTime sosTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sosDate == null) sosDate = LocalDate.now();
        if (sosTime == null) sosTime = LocalTime.now();
    }

    public Long getSosId() { return sosId; }
    public void setSosId(Long sosId) { this.sosId = sosId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDate getSosDate() { return sosDate; }
    public void setSosDate(LocalDate sosDate) { this.sosDate = sosDate; }

    public LocalTime getSosTime() { return sosTime; }
    public void setSosTime(LocalTime sosTime) { this.sosTime = sosTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
