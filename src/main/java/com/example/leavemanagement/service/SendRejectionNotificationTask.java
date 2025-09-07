package com.example.leavemanagement.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendRejectionNotificationTask implements JavaDelegate {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(DelegateExecution execution) {
        String employeeEmail = (String) execution.getVariable("employeeEmail");
        String employeeName = (String) execution.getVariable("employeeName");
        String rejectionReason = (String) execution.getVariable("rejectionReason");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employeeEmail);
        message.setSubject("Your Leave Request Has Been Rejected");
        message.setText("Dear " + employeeName + ",\n\nYour leave request has been rejected.\nReason: " +
                rejectionReason + "\n\nBest regards,\nHR Department");

        mailSender.send(message);
    }
}