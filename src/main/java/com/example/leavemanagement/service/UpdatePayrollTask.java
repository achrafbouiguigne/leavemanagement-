package com.example.leavemanagement.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpdatePayrollTask implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePayrollTask.class);

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Simulate payroll system integration
            String employeeId = (String) execution.getVariable("employeeId");
            String startDate = (String) execution.getVariable("startDate");
            String endDate = (String) execution.getVariable("endDate");
            String leaveType = (String) execution.getVariable("leaveType");

            logger.info("Updating payroll system for employee {}: {} leave from {} to {}",
                    employeeId, leaveType, startDate, endDate);


            if (Math.random() < 0.2) {
                throw new RuntimeException("Payroll system unavailable");
            }

            // Mark payroll as updated in the process variables
            execution.setVariable("payrollUpdated", true);

        } catch (Exception e) {
            logger.error("Error updating payroll system", e);
            throw new RuntimeException("Payroll system unavailable", e);
        }
    }
}