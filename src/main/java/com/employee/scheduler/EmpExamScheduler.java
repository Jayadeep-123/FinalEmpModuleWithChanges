package com.employee.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.employee.service.EmpExamIntegrationService;

@Component
public class EmpExamScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmpExamScheduler.class);

    private final EmpExamIntegrationService integrationService;

    public EmpExamScheduler(EmpExamIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * Scheduled task to fetch exam results for active employees without results.
     * Cron: "0 0 * * * *" means at minute 0 of every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void fetchResultsScheduled() {
        logger.info(">>> Starting Scheduled Exam Result Fetch (every 1 hour) <<<");
        try {
            integrationService.fetchAllResults();
        } catch (Exception e) {
            logger.error("Critical error in exam result scheduler: {}", e.getMessage(), e);
        }
        logger.info(">>> Completed Scheduled Exam Result Fetch <<<");
    }
}
