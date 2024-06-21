package com.safetynet.alerts.dto;

import java.util.List;

public record FireDTO(
        int fireStation,
        List<FireFloodPersonInfoDTO> persons
) {
}
