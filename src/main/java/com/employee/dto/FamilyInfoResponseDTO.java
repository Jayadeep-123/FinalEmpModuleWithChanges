package com.employee.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInfoResponseDTO {
    private String familyPhotoPath;
    private List<FamilyDetailsResponseDTO> familyMembers;
}
