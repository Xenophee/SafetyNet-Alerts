package com.safetynet.alerts.dto;

import java.util.List;

public record ChildInfoDTO(
        String firstName,
        String lastName,
        int age,
        List<String> otherFamilyMembers
) {
}
