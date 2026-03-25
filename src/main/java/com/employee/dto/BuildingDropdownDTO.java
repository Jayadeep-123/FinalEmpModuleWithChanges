package com.employee.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingDropdownDTO {
    private int id;
    private String name;
    private int is_main_building;
}