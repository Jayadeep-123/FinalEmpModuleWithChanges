package com.employee.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.employee.dto.EmpExamDataDTO;
import com.employee.dto.ExamResultDTO;
import com.employee.entity.SkillTestDetails;
import com.employee.repository.SkillTestDetailsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmpExamIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(EmpExamIntegrationService.class);

    // --- API CONFIGURATION ---
    private final String API_BASE_URL = "https://webservice.scaits.net/empExamService";
    // Modified URL as per new requirement
    private final String SAVE_EMP_URL = "https://testreportsapi.scaits.net/scaits/saveEmpBioDetails";
    private final String FETCH_RESULT_URL = "https://testreportsapi.scaits.net/scaits/getEmpTestResult";

    // Credentials from Email
    // private final String API_USERNAME = "QBemp$est";
    // private final String API_PASSWORD = "ScaitsQB";

    private final String API_USERNAME = "ScaitsQB";
    private final String API_PASSWORD = "QBemp$est";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SkillTestDetailsRepository skillTestRepository;
    private final com.employee.repository.SkillTestResultRepository skillTestResultRepository;
    private final com.employee.repository.SkillTestApprovalStatusRepository skillTestApprovalStatusRepository;

    public EmpExamIntegrationService(RestTemplate restTemplate,
            ObjectMapper objectMapper,
            SkillTestDetailsRepository skillTestRepository,
            com.employee.repository.SkillTestResultRepository skillTestResultRepository,
            com.employee.repository.SkillTestApprovalStatusRepository skillTestApprovalStatusRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.skillTestRepository = skillTestRepository;
        this.skillTestResultRepository = skillTestResultRepository;
        this.skillTestApprovalStatusRepository = skillTestApprovalStatusRepository;
    }

    // ==================================================================================
    // 1. SYNC METHOD (Fetch from DB -> Map to DTO -> POST to External API)
    // ==================================================================================
    public String syncEmployeeFromDb(String tempPayrollId) {
        // 1. Fetch using the custom String finder
        SkillTestDetails entity = skillTestRepository.findByTempPayrollId(tempPayrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found with Payroll ID: " + tempPayrollId));

        // 2. Map Entity -> DTO
        EmpExamDataDTO dto = mapEntityToDto(entity);

        // 3. Send to External API
        return pushEmployeeData(entity.getTempPayrollId(), dto);
    }

    // ==================================================================================
    // 2. PREVIEW METHOD (The GET method you requested to check data locally)
    // ==================================================================================
    public EmpExamDataDTO previewEmployeeData(String tempPayrollId) {
        // 1. Fetch using the custom String finder
        SkillTestDetails entity = skillTestRepository.findByTempPayrollId(tempPayrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found with Payroll ID: " + tempPayrollId));

        // 2. Map & Return
        return mapEntityToDto(entity);
    }

    // ==================================================================================
    // 3. FETCH RESULT METHOD (Pull Exam Results from External API & SAVE to DB)
    // ==================================================================================
    public ExamResultDTO fetchExamResult(String empId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Assuming Fetch also needs Auth now? If not, removing this line won't hurt if
            // headers are empty
            // headers.setBasicAuth(API_USERNAME, API_PASSWORD);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            // new param is 'empPayrollId'
            String finalUrl = FETCH_RESULT_URL + "?empPayrollId=" + empId;

            logger.info("Fetching Results from: {}", finalUrl);

            long startTime = System.currentTimeMillis();
            ResponseEntity<ExamResultDTO> response = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    requestEntity,
                    ExamResultDTO.class);
            long endTime = System.currentTimeMillis();
            logger.debug("API call for {} took {} ms", empId, (endTime - startTime));

            ExamResultDTO dto = response.getBody();

            // --- SAVE LOGIC ---
            if (dto != null) {
                saveExamResult(dto);
            }

            return dto;

        } catch (org.springframework.web.client.HttpClientErrorException.UnprocessableEntity e) {
            // Special handling for 422: "This Employee have not attempt the exam"
            // We don't want to print stack traces for this expected scenario
            logger.debug("Employee {} has not attempted the exam yet (422).", empId);
            return null;
        } catch (Exception e) {
            logger.error("Error Fetching Exam Result for {}: {}", empId, e.getMessage());
            return null;
        }
    }

    /**
     * Fetches exam results for all active employees who don't have results yet.
     * Uses parallel stream for faster execution (fetching "all at one time").
     */
    /**
     * Optimized retrieval:
     * 1. Checks local 'result' table first.
     * 2. If exists, returns local data.
     * 3. If not, fetches from external API and saves locally.
     */
    public ExamResultDTO getExamResultLocalOrExternal(String empId) {
        // 1. Check Local DB
        java.util.Optional<com.employee.entity.SkillTestResult> local = skillTestResultRepository
                .findLatestActiveByPayrollId(empId);

        if (local.isPresent()) {
            logger.info("Found local exam result for {}, skipping API call.", empId);
            return mapEntityToExamResultDto(local.get());
        }

        // 2. Not found locally -> Fetch from External API
        logger.info("No local result found for {}, fetching from external API.", empId);
        return fetchExamResult(empId);
    }

    private ExamResultDTO mapEntityToExamResultDto(com.employee.entity.SkillTestResult entity) {
        ExamResultDTO dto = new ExamResultDTO();
        dto.setPayrollId(entity.getSkillTestDetlId().getTempPayrollId());
        dto.setExamDate(entity.getExamDate() != null ? entity.getExamDate().toString() : null);
        dto.setTotalMarks(String.valueOf(entity.getTotalMarks()));
        dto.setTotalQuestions(String.valueOf(entity.getNoOfQuestion()));
        dto.setAttempted(String.valueOf(entity.getNoOfQuesAttempt()));
        dto.setUnAttempted(String.valueOf(entity.getNoOfQuesUnattempt()));
        dto.setCorrect(String.valueOf(entity.getNoOfQuesCorrect()));
        dto.setWrong(String.valueOf(entity.getNoOfQuesWrong()));

        if (entity.getSkillTestDetlId().getSubject() != null) {
            dto.setSubject(entity.getSkillTestDetlId().getSubject().getSubject_name());
        }

        return dto;
    }

    public void fetchAllResults() {
        // 1. Fetch active skill test details that DON'T have a result yet
        java.util.List<SkillTestDetails> activeEmployees = skillTestRepository.findActiveWithoutResults();

        if (activeEmployees == null || activeEmployees.isEmpty()) {
            logger.info("No active employees found needing results synchronization.");
            return;
        }

        long startTime = System.currentTimeMillis();
        logger.info(">>> Starting Parallel Fetch for {} employees <<<", activeEmployees.size());

        // 2. Process in parallel
        activeEmployees.parallelStream().forEach(emp -> {
            String tempId = emp.getTempPayrollId();
            if (tempId != null && !tempId.trim().isEmpty()) {
                long empStartTime = System.currentTimeMillis();
                fetchExamResult(tempId); // This method handles its own try-catch and logging
                long empDuration = System.currentTimeMillis() - empStartTime;
                logger.debug("Total sync processing for {} took {} ms", tempId, empDuration);
            }
        });

        long totalDuration = System.currentTimeMillis() - startTime;
        logger.info(">>> Completed Parallel Fetch for {} employees in {} ms <<<", activeEmployees.size(), totalDuration);
    }

    private void saveExamResult(ExamResultDTO dto) {
        if (dto == null || dto.getPayrollId() == null)
            return;

        try {
            // Final Double-Check: Skip if an active result already exists locally
            if (skillTestResultRepository.findLatestActiveByPayrollId(dto.getPayrollId()).isPresent()) {
                logger.info("Active result already exists for {}, skipping duplicate save.", dto.getPayrollId());
                return;
            }

            // 1. Find Employee locally
            SkillTestDetails employee = skillTestRepository.findByTempPayrollId(dto.getPayrollId())
                    .orElseThrow(
                            () -> new RuntimeException("Employee not found for Result Sync: " + dto.getPayrollId()));

            // 2. Map DTO -> Entity
            com.employee.entity.SkillTestResult resultEntity = new com.employee.entity.SkillTestResult();

            resultEntity.setSkillTestDetlId(employee);
            resultEntity.setEmpName(employee.getFirstName() + " " + employee.getLastName());

            // --- INACTIVATION LOGIC ---
            // Find existing ACTIVE results for this employee
            java.util.List<com.employee.entity.SkillTestResult> existingActive = skillTestResultRepository
                    .findBySkillTestDetlIdAndIsActive(employee, 1);

            if (existingActive != null && !existingActive.isEmpty()) {
                for (com.employee.entity.SkillTestResult oldResult : existingActive) {
                    oldResult.setIsActive(0); // Deactivate
                }
                skillTestResultRepository.saveAll(existingActive);
                logger.info("Deactivated {} old results for: {}", existingActive.size(), dto.getPayrollId());
            }
            // --------------------------

            // Parse Date (dd-MM-yyyy -> SQL Date)
            try {
                if (dto.getExamDate() != null && !dto.getExamDate().trim().isEmpty()) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    java.util.Date parsed = sdf.parse(dto.getExamDate());
                    resultEntity.setExamDate(new java.sql.Date(parsed.getTime()));
                } else {
                    logger.warn("Exam date is missing for {}, using current date.", dto.getPayrollId());
                    resultEntity.setExamDate(new java.sql.Date(System.currentTimeMillis()));
                }
            } catch (Exception e) {
                logger.error("Date Parse Error for {}: {}", dto.getPayrollId(), e.getMessage());
                resultEntity.setExamDate(new java.sql.Date(System.currentTimeMillis()));
            }

            // Parse Numbers (Handle Strings "16.0", "30", etc.)
            resultEntity.setNoOfQuestion(parseInteger(dto.getTotalQuestions()));
            resultEntity.setNoOfQuesAttempt(parseInteger(dto.getAttempted()));
            resultEntity.setNoOfQuesUnattempt(parseInteger(dto.getUnAttempted()));
            resultEntity.setNoOfQuesCorrect(parseInteger(dto.getCorrect()));
            resultEntity.setNoOfQuesWrong(parseInteger(dto.getWrong()));

            // Total Marks (Stored as int in DB, but might come as "16.0" from API)
            resultEntity.setTotalMarks((int) parseDouble(dto.getTotalMarks()));

            // Hardcoded / Default fields
            resultEntity.setIsActive(1);
            resultEntity.setCreatedBy(dto.getCreatedBy()); // Passed from DTO as per instruction

            // Set Approval Status (ID 1: "Skill Test Approval")
            skillTestApprovalStatusRepository.findById(1).ifPresent(resultEntity::setSkillTestApprovalStatus);

            long saveStartTime = System.currentTimeMillis();
            // Save
            skillTestResultRepository.save(resultEntity);
            long saveDuration = System.currentTimeMillis() - saveStartTime;
            logger.info("Saved Exam Result for: {} (DB save took {} ms)", dto.getPayrollId(), saveDuration);

        } catch (Exception e) {
            logger.error("Error Saving Local Result for {}: {}", dto.getPayrollId(), e.getMessage());
        }
    }

    private int parseInteger(String val) {
        try {
            return (int) Double.parseDouble(val); // Handle "30.0" or "30"
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ==================================================================================
    // HELPER: CENTRALIZED MAPPING LOGIC (DB Entity -> API DTO)
    // ==================================================================================
    private EmpExamDataDTO mapEntityToDto(SkillTestDetails entity) {
        EmpExamDataDTO dto = new EmpExamDataDTO();

        // --- Direct Fields ---
        dto.setTempId(entity.getTempPayrollId()); // Renamed
        dto.setName(entity.getFirstName());
        dto.setSurname(entity.getLastName());

        // userName = "SURNAME FIRSTNAME" (Based on sample: "GADDAM MAHESH KUMAR")
        String fullName = (entity.getLastName() != null ? entity.getLastName() : "") + " " +
                (entity.getFirstName() != null ? entity.getFirstName() : "");
        dto.setUserName(fullName.trim());

        // DOB: Avoid empty string "" which crashes DBs. Send null or formatted date.
        if (entity.getDob() != null) {
            dto.setDob(entity.getDob().toString());
        } else {
            dto.setDob(null); // Explicitly null rather than ""
        }

        // Mobile: Default to 0L if null
        dto.setMobileNo(entity.getContact_number() != null ? entity.getContact_number() : 0L);
        dto.setEmail(entity.getEmail());

        // Passwords
        String rawPassword = entity.getPassword();
        dto.setPasswordDecrypt(rawPassword); // Send raw in 'passwordDecrypt'
        dto.setPassword(md5Hash(rawPassword)); // Send hash in 'password'

        // Hardcoded status as per requirement
        dto.setStatus("Skill Test Approval");

        dto.setStudentType("EMPLOYEE");

        // Gender
        if (entity.getGender() != null) {
            dto.setGender(entity.getGender().getGenderName());
        } else {
            dto.setGender("MALE"); // Fallback
        }

        // Subject
        if (entity.getSubject() != null) {
            dto.setSubject(entity.getSubject().getSubject_name());
        }

        // EmpLevel (Renamed from Program)
        if (entity.getEmployeeLevel() != null) {
            dto.setEmpLevel(entity.getEmployeeLevel().getLevel_name());
        }

        // Campus
        if (entity.getCampus() != null) {
            dto.setCampusId(Long.valueOf(entity.getCampus().getCampusId()));
            dto.setCampusName(entity.getCampus().getCampusName());
        }

        // City
        if (entity.getCity() != null) {
            dto.setCityId(Long.valueOf(entity.getCity().getCityId()));
            dto.setCityName(entity.getCity().getCityName());
        }

        // Building removed from payload

        // Group
        if (entity.getOrientationGroup() != null) {
            dto.setGroup(entity.getOrientationGroup().getGroupName());
        } else {
            dto.setGroup("Mpc"); // Default from sample if missing
        }

        return dto;
    }

    // Helper for MD5
    private String md5Hash(String input) {
        if (input == null)
            return null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            java.math.BigInteger no = new java.math.BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ==================================================================================
    // HELPER: PUSH DATA TO API (The actual POST request)
    // ==================================================================================
    private String pushEmployeeData(String empId, EmpExamDataDTO employeeData) {
        try {
            // Log JSON for debugging
            String jsonPreview = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employeeData);
            System.out.println("--- SYNCING EMP: " + empId + " ---");
            System.out.println(jsonPreview);
            System.out.println("--------------------------------");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // UPDATED: Basic Auth
            headers.setBasicAuth(API_USERNAME, API_PASSWORD);

            HttpEntity<EmpExamDataDTO> requestEntity = new HttpEntity<>(employeeData, headers);
            String finalUrl = SAVE_EMP_URL + "?empId=" + empId;

            ResponseEntity<String> response = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            return "Success: " + response.getBody();

        } catch (Exception e) {
            System.err.println("API Call Failed: " + e.getMessage());
            return "Failed: " + e.getMessage();
        }
    }
}