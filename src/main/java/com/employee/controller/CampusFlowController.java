package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.EmployeeDropdownDTO;
import com.employee.dto.EmployeeFullDetailsDTO;
import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.BusinessType;
import com.employee.entity.CampusProfileView;
import com.employee.service.CampusFlowService;

@RestController
@RequestMapping("/api/campus-flow")
@CrossOrigin("*")
public class CampusFlowController {

    @Autowired
    private CampusFlowService campusFlowService;

    @GetMapping("/getcampus/{cityId}/{businessId}")
    public ResponseEntity<List<GenericDropdownDTO>> getCampusesByCityAndBusinessId(
            @PathVariable int cityId,
            @PathVariable int businessId) {
        List<GenericDropdownDTO> campuses = campusFlowService.getCampusesByCityAndBusinessForDropdown(cityId,
                businessId);

        if (campuses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(campuses, HttpStatus.OK);
    }

    @GetMapping("/getemployees/{departmentId}/{campusId}")
    public ResponseEntity<List<EmployeeDropdownDTO>> getEmployeesByDepartmentAndCampus(
            @PathVariable int departmentId,
            @PathVariable int campusId) {

        List<EmployeeDropdownDTO> employees = campusFlowService.getActiveEmployeesByDepartmentAndCampusForDropdown(
                departmentId, campusId);

        if (employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/campus-profile/{cmpsId}")
    public ResponseEntity<List<CampusProfileView>> getCampusProfileDetails(@PathVariable int cmpsId) {

        List<CampusProfileView> campusDetails = campusFlowService.getCampusDetailsByCmpsId(cmpsId);

        if (campusDetails.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(campusDetails, HttpStatus.OK);
    }

    @GetMapping("/byCampus/{campusId}")
    public ResponseEntity<List<EmployeeFullDetailsDTO>> getEmployeeFullDetailsByCampusId(
            @PathVariable int campusId) {

        List<EmployeeFullDetailsDTO> details = campusFlowService.getEmployeeFullDetailsByCampusId(campusId);

        if (details.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping("getallbusineestype")
    public ResponseEntity<List<BusinessType>> getAllBusinessTypes() {

        List<BusinessType> businessTypes = campusFlowService.getAllBusinessTypes();

        if (businessTypes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(businessTypes, HttpStatus.OK);
    }

    @GetMapping("/alldepartments")
    public List<GenericDropdownDTO> getAllActiveDepartments(@RequestParam(required = false) String categoryName) {
        return campusFlowService.getActiveDepartments(categoryName);
    }
}
