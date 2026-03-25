package com.employee.controller;
 
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.employee.dto.ValidationRequestDTO;
import com.employee.service.EmployeeValidationService;
 
@RestController
@RequestMapping("/api/employee/validate")
@CrossOrigin("*")
public class EmployeeValidationController {
 
    @Autowired
    private EmployeeValidationService validationService;
 
    @PostMapping
    public ResponseEntity<Map<String, Object>> validateField(
            @RequestBody ValidationRequestDTO request) {
        Map<String, Object> result = validationService.validateField(
                request.getFieldName(),
                request.getValue());
        return ResponseEntity.ok(result);
    }
}
 
 