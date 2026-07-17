package com.womensafety.service;

import com.womensafety.dto.SOSRequest;
import com.womensafety.model.SOS;
import com.womensafety.repository.SOSRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SOSService {

    private final SOSRepository sosRepository;

    public SOSService(SOSRepository sosRepository) {
        this.sosRepository = sosRepository;
    }

    public SOS recordSOS(SOSRequest request) {
        SOS sos = new SOS();
        sos.setUserId(request.getUserId());
        sos.setLatitude(request.getLatitude());
        sos.setLongitude(request.getLongitude());
        return sosRepository.save(sos);
    }

    public List<SOS> getHistory(Long userId) {

        return sosRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public SOS updateStatus(Long sosId, String status) {
        SOS sos = sosRepository.findById(sosId)
                .orElseThrow(() -> new RuntimeException("SOS record not found"));
        sos.setStatus(status);
        return sosRepository.save(sos);
    }

}
