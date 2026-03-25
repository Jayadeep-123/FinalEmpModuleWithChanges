package com.employee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.EmpExamDataDTO;
import com.employee.dto.ExamResultDTO;
import com.employee.service.EmpExamIntegrationService;

@RestController
@RequestMapping("/api/integration")
public class EmpExamController {

    private final EmpExamIntegrationService integrationService;

    // Constructor Injection
    public EmpExamController(EmpExamIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    // =====================================================================
    // 1. SYNC ENDPOINT (POST)
    // Usage: POST http://localhost:8080/api/integration/sync-db-employee?id=123
    // Action: Reads DB -> Maps Data -> Sends to Epraghna API
    // =====================================================================
    @PostMapping("/sync-db-employee")
    public ResponseEntity<String> syncEmployeeFromDb(@RequestParam String id) {
        // 'id' is now a String (e.g. "TEMP5540045")
        String result = integrationService.syncEmployeeFromDb(id);
        return ResponseEntity.ok(result);
    }

    // =====================================================================
    // 2. PREVIEW ENDPOINT (GET)
    // Usage: GET http://localhost:8080/api/integration/check-data?id=123
    // Action: Reads DB -> Returns JSON to Browser (Does NOT send to API)
    // =====================================================================
    @GetMapping("/check-data")
    public ResponseEntity<EmpExamDataDTO> checkData(@RequestParam String id) {
        // 'id' is now a String
        EmpExamDataDTO data = integrationService.previewEmployeeData(id);
        return ResponseEntity.ok(data);
    }

    // =====================================================================
    // 3. FETCH RESULT ENDPOINT (GET)
    // Usage: GET http://localhost:8080/api/integration/get-result?empId=376487
    // Action: Calls Epraghna API -> Returns Exam Results
    // =====================================================================
    @GetMapping("/get-result")
    public ResponseEntity<ExamResultDTO> getExamResult(@RequestParam String empId) {
        // Optimized: Returns local data if present, otherwise fetches from API
        ExamResultDTO result = integrationService.getExamResultLocalOrExternal(empId);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // =====================================================================
    // 4. FETCH ALL RESULTS ENDPOINT (GET)
    // Usage: GET http://localhost:8080/api/integration/fetch-all-results
    // Action: Triggers parallel sync for ALL qualifying employees
    // =====================================================================
    @GetMapping("/fetch-all-results")
    public ResponseEntity<String> fetchAllResults() {
        integrationService.fetchAllResults();
        return ResponseEntity.ok("Bulk synchronization started in the background.");
    }
}