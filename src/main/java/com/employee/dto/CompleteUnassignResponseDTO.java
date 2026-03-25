package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Complete Unassign endpoint.
 * 
 * Contains the current assignments before unassigning (for frontend
 * auto-population)
 * and confirmation message after unassignment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUnassignResponseDTO {

    /**
     * Employee payroll ID
     */
    private String payrollId;

    /**
     * Current manager ID (before unassignment)
     */
    private Integer managerId;

    /**
     * Current manager name (before unassignment)
     */
    private String managerName;

    /**
     * Current reporting manager ID (before unassignment)
     */
    private Integer reportingManagerId;

    /**
     * Current reporting manager name (before unassignment)
     */
    private String reportingManagerName;

    /**
     * Primary campus ID (before unassignment)
     */
    private Integer primaryCampusId;

    /**
     * Primary campus name (before unassignment)
     */
    private String primaryCampusName;

    /**
     * List of shared campus details (before unassignment)
     */
    private List<SharedCampusInfo> sharedCampuses;

    /**
     * List of campus roles from sce_cmps_emp (before unassignment)
     */
    private List<SharedCampusInfo> campusRoles;

    /**
     * Success message
     */
    private String message;

    /**
     * Inner class for shared campus information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SharedCampusInfo {
        private Integer campusId;
        private String campusName;
    }
}
