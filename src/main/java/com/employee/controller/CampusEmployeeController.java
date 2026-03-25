package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.CampusEmployeeDTO;
import com.employee.service.CampusEmployeeService;

@RestController
@RequestMapping("/api/campus-employee")
@CrossOrigin("*")
public class CampusEmployeeController {

    @Autowired
    private CampusEmployeeService campusEmployeeService;

    @PostMapping("/assign-multiple")
    public ResponseEntity<List<CampusEmployeeDTO>> assignCampusesToEmployee(
            @RequestBody List<CampusEmployeeDTO> campusEmployeeDTOs) {
        List<CampusEmployeeDTO> savedDTOs = campusEmployeeService.assignCampusesToEmployee(campusEmployeeDTOs);
        return new ResponseEntity<>(savedDTOs, HttpStatus.CREATED);
    }

    @PostMapping("/unassign-multiple")
    public ResponseEntity<List<CampusEmployeeDTO>> unassignCampusesFromEmployee(
            @RequestBody List<CampusEmployeeDTO> campusEmployeeDTOs) {
        List<CampusEmployeeDTO> updatedDTOs = campusEmployeeService.unassignCampusesFromEmployee(campusEmployeeDTOs);
        return new ResponseEntity<>(updatedDTOs, HttpStatus.OK);
    }

    @org.springframework.web.bind.annotation.GetMapping("/location/{empId}")
    public ResponseEntity<com.employee.dto.EmployeeLocationDTO> getEmployeeLocation(
            @org.springframework.web.bind.annotation.PathVariable("empId") Integer empId) {
        com.employee.dto.EmployeeLocationDTO locationDTO = campusEmployeeService.getEmployeeLocation(empId);
        return new ResponseEntity<>(locationDTO, HttpStatus.OK);
    }
}
