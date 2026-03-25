package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class EmployeeCurrentInfoDTO {
//
//    private String employeeName;
//    private Date dateOfJoining;
//    private String hiredBy;
//    private String referredBy;
//    private List<SubjectInfo> subjects;
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class SubjectInfo {
//        private String subjectName;
//        private Integer agreedPerWeek;
//       }
//}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCurrentInfoDTO {

    private String employeeName;
    private Date dateOfJoining;
    private String hiredBy;
    private String referredBy;

    // ✅ Move these fields to the main DTO
    // (Remove "List<SubjectInfo> subjects")
    private String subjectName;
    private Integer agreedPerWeek; 
}