package com.safetynet.alerts.dto;

import java.util.List;

public record StationCoverageDTO(
        int adults,
        int children,
        List<StationCoveragePersonInfoDTO> persons
) {
}
