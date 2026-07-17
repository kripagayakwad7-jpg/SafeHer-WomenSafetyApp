package com.womensafety.app.models;

public class SOS {
    private Long sosId;
    private Long userId;
    private Double latitude;
    private Double longitude;
    private String sosDate;
    private String sosTime;

    public Long getSosId() { return sosId; }
    public void setSosId(Long sosId) { this.sosId = sosId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getSosDate() { return sosDate; }
    public void setSosDate(String sosDate) { this.sosDate = sosDate; }

    public String getSosTime() { return sosTime; }
    public void setSosTime(String sosTime) { this.sosTime = sosTime; }
}
