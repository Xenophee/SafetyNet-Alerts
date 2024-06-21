package com.safetynet.alerts.dto;

import java.util.List;
import java.util.Map;

public record FloodDTO(
        int fireStation,
        Map<String, List<FireFloodPersonInfoDTO>> personsByAddress
) {
}
