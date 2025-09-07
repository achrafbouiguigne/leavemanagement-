package com.example.leavemanagement.service;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest.LeaveStatus;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RejectLeaveTask implements JavaDelegate {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        LeaveRequest leaveRequest = leaveRequestRepository.findByProcessInstanceId(processInstanceId)
                .stream().findFirst().orElse(null);

        if (leaveRequest != null) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
            String rejectionReason = (String) execution.getVariable("rejectionReason");
            leaveRequest.setRejectionReason(rejectionReason);
            leaveRequestRepository.save(leaveRequest);
        }
    }
}