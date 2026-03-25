package com.employee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.BulkManagerMappingDTO;
import com.employee.dto.BulkUnmappingDTO;
import com.employee.dto.CompleteUnassignDTO;
import com.employee.dto.CompleteUnassignResponseDTO;
import com.employee.dto.BulkCompleteUnassignDTO;
import com.employee.dto.EmployeeBatchCampusDTO;
import com.employee.dto.EmployeeCampusAddressDTO;
import com.employee.dto.ManagerMappingDTO;
import com.employee.dto.SelectiveBulkUnmappingDTO;
import com.employee.dto.SelectiveUnmappingDTO;
import com.employee.dto.UnmappingDTO;
import com.employee.service.ManagerMappingService;

/**
 * Controller for Manager Mapping functionality.
 * Handles employee mapping based on City → Campus → Department → Designation
 * hierarchy
 * and updates work starting dates.
 */
@RestController
@RequestMapping("/api/manager-mapping")
@CrossOrigin("*")
public class ManagerMappingController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerMappingController.class);

    @Autowired
    private ManagerMappingService managerMappingService;

    /**
     * POST endpoint to map employee and update their details.
     * 
     * Flow:
     * 1. Validates City
     * 2. Validates Campus (must belong to City)
     * 3. Validates Department exists and is active (master table - independent)
     * 4. Validates Designation exists and is active
     * 5. Finds Employee by payrollId
     * 6. Validates Employee is active
     * 7. Validates Manager (managerId) exists and is active (if provided)
     * 8. Validates Reporting Manager (reportingManagerId) exists and is active (if
     * provided)
     * 9. Updates employee: campus, department, designation, manager, reporting
     * manager, work starting date, remarks
     * 
     * Note: All exceptions are handled by GlobalExceptionHandler
     * 
     * @param mappingDTO Request body containing cityId, campusId, departmentId,
     *                   designationId,
     *                   payrollId (required), managerId (optional),
     *                   reportingManagerId (optional),
     *                   workStartingDate, remark (optional), and updatedBy
     * @return The same ManagerMappingDTO that was passed in
     */
    @PostMapping("/map-employees")
    public ResponseEntity<ManagerMappingDTO> mapEmployeesAndUpdateWorkDate(@RequestBody ManagerMappingDTO mappingDTO) {
        logger.info("Received manager mapping request: {}", mappingDTO);

        // Perform the mapping and update (validation and exception handling done in
        // service and GlobalExceptionHandler)
        ManagerMappingDTO response = managerMappingService.mapEmployeesAndUpdateWorkDate(mappingDTO);

        logger.info("Successfully updated employee with payrollId: {}", response.getPayrollId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST endpoint to map multiple employees and update their details in bulk.
     * 
     * Flow:
     * 1. Validates City
     * 2. Validates Campus (must belong to City)
     * 3. Validates Department exists and is active (master table - independent)
     * 4. Validates Designation exists and is active
     * 5. Validates Manager (managerId) exists and is active (if provided)
     * 6. Validates Reporting Manager (reportingManagerId) exists and is active (if
     * provided)
     * 7. For each payrollId in the list:
     * - Finds Employee by payrollId
     * - Validates Employee is active
     * - Updates employee: campus, department, designation, manager, reporting
     * manager, work starting date, remarks
     * 
     * Note: All exceptions are handled by GlobalExceptionHandler
     * 
     * @param bulkMappingDTO Request body containing cityId, campusId, departmentId,
     *                       designationId,
     *                       payrollIds (list of payroll IDs, required), managerId
     *                       (optional), reportingManagerId (optional),
     *                       workStartingDate, remark (optional), and updatedBy
     * @return The same BulkManagerMappingDTO that was passed in
     */
    @PostMapping("/map-multiple-employees")
    public ResponseEntity<BulkManagerMappingDTO> mapMultipleEmployeesAndUpdateWorkDate(
            @RequestBody BulkManagerMappingDTO bulkMappingDTO) {
        logger.info("Received bulk manager mapping request for {} payrollIds",
                bulkMappingDTO.getPayrollIds() != null ? bulkMappingDTO.getPayrollIds().size() : 0);

        // Perform the bulk mapping and update (validation and exception handling done
        // in service and GlobalExceptionHandler)
        BulkManagerMappingDTO response = managerMappingService.mapMultipleEmployeesAndUpdateWorkDate(bulkMappingDTO);

        logger.info("Successfully processed bulk update for {} payrollIds",
                response.getPayrollIds() != null ? response.getPayrollIds().size() : 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST endpoint to unmap (remove) manager and/or reporting manager from an
     * employee.
     * Does not require department or designation - but requires city, campus, and
     * last date of working.
     * 
     * Flow:
     * 1. Validates City exists
     * 2. Validates Campus (must belong to City)
     * 3. Finds Employee by payrollId
     * 4. Validates Employee is active
     * 5. Updates campus
     * 6. Sets manager_id to null if provided managerId matches current manager
     * 7. Sets reporting_manager_id to null if provided reportingManagerId matches
     * current reporting manager
     * 8. Updates last date of working (contract end date)
     * 9. Updates remarks (set to null if empty)
     * 10. Updates employee
     * 
     * Note: All exceptions are handled by GlobalExceptionHandler
     * 
     * @param unmappingDTO Request body containing cityId (required), campusId
     *                     (required),
     *                     payrollId (required), managerId (optional, employee ID to
     *                     unmap; pass 0 or null to skip),
     *                     reportingManagerId (optional, employee ID to unmap; pass
     *                     0 or null to skip),
     *                     lastDate (required), remark (optional), and updatedBy
     * @return The same UnmappingDTO that was passed in
     */
    @PostMapping("/unmap-employee")
    public ResponseEntity<UnmappingDTO> unmapEmployee(@RequestBody UnmappingDTO unmappingDTO) {
        logger.info("Received unmapping request for payrollId: {}", unmappingDTO.getPayrollId());

        // Perform the unmapping (validation and exception handling done in service and
        // GlobalExceptionHandler)
        UnmappingDTO response = managerMappingService.unmapEmployee(unmappingDTO);

        logger.info("Successfully unmapped employee with payrollId: {}", response.getPayrollId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST endpoint to unmap (remove) manager and/or reporting manager from
     * multiple employees in bulk.
     * Does not require department or designation - but requires city, campus, and
     * last date of working.
     * 
     * Flow:
     * 1. Validates City exists
     * 2. Validates Campus (must belong to City)
     * 3. For each payrollId in the list:
     * - Finds Employee by payrollId
     * - Validates Employee is active
     * - Updates campus
     * - Sets manager_id to null if provided managerId matches current manager
     * - Sets reporting_manager_id to null if provided reportingManagerId matches
     * current reporting manager
     * - Updates last date of working (contract end date)
     * - Updates remarks (set to null if empty)
     * - Updates employee
     * 
     * Note: All exceptions are handled by GlobalExceptionHandler
     * 
     * @param bulkUnmappingDTO Request body containing cityId (required), campusId
     *                         (required),
     *                         payrollIds (list of payroll IDs, required),
     *                         managerId (optional, employee ID to unmap; pass 0 or
     *                         null to skip),
     *                         reportingManagerId (optional, employee ID to unmap;
     *                         pass 0 or null to skip),
     *                         lastDate (required), remark (optional), and updatedBy
     * @return The same BulkUnmappingDTO that was passed in
     */
    @PostMapping("/unmap-multiple-employees")
    public ResponseEntity<BulkUnmappingDTO> unmapMultipleEmployees(@RequestBody BulkUnmappingDTO bulkUnmappingDTO) {
        logger.info("Received bulk unmapping request for {} payrollIds",
                bulkUnmappingDTO.getPayrollIds() != null ? bulkUnmappingDTO.getPayrollIds().size() : 0);

        // Perform the bulk unmapping (validation and exception handling done in service
        // and GlobalExceptionHandler)
        BulkUnmappingDTO response = managerMappingService.unmapMultipleEmployees(bulkUnmappingDTO);

        logger.info("Successfully processed bulk unmapping for {} payrollIds",
                response.getPayrollIds() != null ? response.getPayrollIds().size() : 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST endpoint to selectively unmap manager and/or reporting manager from
     * multiple employees in bulk.
     * Uses boolean flags to control what gets unmapped.
     * 
     * @param dto Request body containing cityId, campusIds, payrollIds,
     *            unmapManager, managerId, unmapReportingManager,
     *            reportingManagerId, lastDate, remark, and updatedBy
     * @return The same SelectiveBulkUnmappingDTO that was passed in
     */
    @PostMapping("/unmap-multiple-employees-selective")
    public ResponseEntity<SelectiveBulkUnmappingDTO> unmapMultipleEmployeesSelective(
            @RequestBody SelectiveBulkUnmappingDTO dto) {
        logger.info("Received enhanced bulk unmapping request for {} payrollIds",
                dto.getPayrollIds() != null ? dto.getPayrollIds().size() : 0);

        SelectiveBulkUnmappingDTO response = managerMappingService.selectiveUnmapMultipleEmployees(dto);

        logger.info("Successfully processed enhanced bulk unmapping for {} payrollIds",
                response.getPayrollIds() != null ? response.getPayrollIds().size() : 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST endpoint to complete unassign assignments for multiple employees in
     * bulk.
     * Clears manager, reporting manager, primary campus, and all shared campuses.
     * 
     * @param dto BulkCompleteUnassignDTO containing payrollIds, remark, and
     *            updatedBy
     * @return Success message
     */
    @PostMapping("/bulk-complete-unmap")
    public ResponseEntity<List<CompleteUnassignResponseDTO>> bulkCompleteUnmap(
            @RequestBody BulkCompleteUnassignDTO dto) {
        logger.info("Received bulk complete unassign request for {} payrollIds",
                dto.getPayrollIds() != null ? dto.getPayrollIds().size() : 0);

        List<CompleteUnassignResponseDTO> response = managerMappingService.bulkCompleteUnmap(dto);

        logger.info("Successfully processed bulk complete unassignment");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/batch-campus-address")
    public ResponseEntity<List<EmployeeBatchCampusDTO>> getBatchCampusAddresses(
            @RequestBody List<String> payrollIds) {
        if (payrollIds == null || payrollIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<EmployeeBatchCampusDTO> results = managerMappingService.getMultipleCampusAddresses(payrollIds);
        return ResponseEntity.ok(results);
    }

    /**
     * POST endpoint to selectively unmap manager and/or reporting manager.
     * Uses boolean flags to control what gets unmapped.
     * 
     * Flow:
     * 1. Finds Employee by payrollId
     * 2. Validates Employee is active
     * 3. If unmapManager is true: Removes manager assignment
     * 4. If unmapReportingManager is true: Removes reporting manager assignment
     * 5. Updates remarks if provided
     * 6. Updates employee with audit fields
     * 
     * Note: All exceptions are handled by GlobalExceptionHandler
     * 
     * @param dto Request body containing:
     *            - payrollId (required): Employee to unmap
     *            - unmapManager (boolean): If true, remove manager assignment
     *            - managerId (optional): Manager ID to verify before unmapping
     *            - unmapReportingManager (boolean): If true, remove reporting
     *            manager
     *            - reportingManagerId (optional): Reporting manager ID to verify
     *            before unmapping
     *            - remark (optional): Reason for unmapping
     *            - updatedBy (required): User making the change
     * @return The same SelectiveUnmappingDTO that was passed in
     */
    @PostMapping("/selective-unmap")
    public ResponseEntity<SelectiveUnmappingDTO> selectiveUnmapEmployee(@RequestBody SelectiveUnmappingDTO dto) {
        logger.info(
                "Received selective unmapping request for payrollId: {} - unmapManager: {}, unmapReportingManager: {}",
                dto.getPayrollId(), dto.getUnmapManager(), dto.getUnmapReportingManager());

        // Perform the selective unmapping (validation and exception handling done in
        // service and GlobalExceptionHandler)
        SelectiveUnmappingDTO response = managerMappingService.selectiveUnmapEmployee(dto);

        logger.info("Successfully processed selective unmapping for payrollId: {}", response.getPayrollId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @PutMapping("/campus-name")
    // public ResponseEntity<?> updateCampusName(@RequestParam Integer campusId,
    // @RequestParam String campusName) {
    // try {
    // managerMappingService.updateCampusName(campusId, campusName);
    // return ResponseEntity.ok("Campus name updated successfully");
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("Error updating campus name: " + e.getMessage());
    // }
    // }

    /**
     * POST endpoint to completely unassign all assignments from an employee.
     * 
     * This endpoint:
     * 1. Returns current assignments (for frontend auto-population)
     * 2. Unassigns manager, reporting manager, primary campus
     * 3. Deactivates all shared campus records
     * 
     * Flow:
     * - Frontend opens form and calls this endpoint
     * - Response contains current manager, reporting manager, and campuses
     * - Frontend auto-populates the form with this data
     * - User reviews and clicks Confirm
     * - All assignments are cleared
     * 
     * @param dto Request body (same structure as selective-unmap):
     *            - payrollId (required): Employee to unassign
     *            - cityId (optional): City ID
     *            - campusIds (optional): List of campus IDs
     *            - lastDate (optional): Last working date
     *            - remark (optional): Reason for complete unassignment
     *            - updatedBy (required): User making the change
     * @return CompleteUnassignResponseDTO with current assignments and success
     *         message
     */
    @PostMapping("/complete-unassign")
    public ResponseEntity<CompleteUnassignResponseDTO> completeUnassign(@RequestBody CompleteUnassignDTO dto) {
        logger.info("Received complete unassign request for payrollId: {}", dto.getPayrollId());

        // Perform complete unassignment (validation and exception handling done in
        // service and GlobalExceptionHandler)
        CompleteUnassignResponseDTO response = managerMappingService.completeUnassign(dto);

        logger.info("Successfully completed unassignment for payrollId: {}", response.getPayrollId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}