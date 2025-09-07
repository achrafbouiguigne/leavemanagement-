package com.example.leavemanagement.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendReminderTask implements JavaDelegate {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(DelegateExecution execution) {
        String hodEmail = (String) execution.getVariable("hodEmail");
        String hodName = (String) execution.getVariable("hodName");
        String employeeName = (String) execution.getVariable("employeeName");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(hodEmail);
        message.setSubject("Reminder: Pending Leave Approval");
        message.setText("Dear " + hodName + ",\n\nYou have a pending leave request from " +
                employeeName + " that requires your attention.\n\nPlease log in to the system to take action.\n\nBest regards,\nLeave Management System");

        mailSender.send(message);
    }
}