package com.example.leavemanagement.service;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.UserRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LeaveProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final UserRepository userRepository;

    public LeaveProcessService(RuntimeService runtimeService,
                               TaskService taskService,
                               UserRepository userRepository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    public void startLeaveProcess(LeaveRequest leaveRequest) {
        User employee = leaveRequest.getEmployee();
        User manager = employee.getManager();
        User hod = findHodForEmployee(employee);

        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeId", employee.getId().toString());
        variables.put("employeeName", employee.getFullName());
        variables.put("employeeEmail", employee.getEmail());
        variables.put("managerId", manager != null ? manager.getId().toString() : "");
        variables.put("hodId", hod != null ? hod.getId().toString() : "");
        variables.put("leaveType", leaveRequest.getType().toString());
        variables.put("startDate", leaveRequest.getStartDate().toString());
        variables.put("endDate", leaveRequest.getEndDate().toString());
        variables.put("reason", leaveRequest.getReason());
        variables.put("duration", leaveRequest.getDuration());

        // Start the process instance
        var processInstance = runtimeService.startProcessInstanceByKey("leaveProcess", variables);

        // Update leave request with process instance ID
        leaveRequest.setProcessInstanceId(processInstance.getId());
    }

    private User findHodForEmployee(User employee) {
        // Find the HOD for the employee's department
        // This is a simplified implementation - in a real system, you'd have proper department mapping
        return userRepository.findHeadsOfDepartment().stream()
                .findFirst()
                .orElse(null);
    }

    public List<Task> getTasksForUser(String username) {
        return taskService.createTaskQuery()
                .taskAssignee(username)
                .orderByTaskCreateTime().desc()
                .list();
    }

    public List<Task> getTasksForGroup(String group) {
        return taskService.createTaskQuery()
                .taskCandidateGroup(group)
                .orderByTaskCreateTime().desc()
                .list();
    }

    public void completeTask(String taskId, String decision, String comments) {
        Map<String, Object> variables = new HashMap<>();

        if (taskId.startsWith("managerApprovalTask")) {
            variables.put("managerDecision", decision);
            if (comments != null && !comments.trim().isEmpty()) {
                variables.put("managerComments", comments);
            }
            if ("reject".equals(decision)) {
                variables.put("rejectionReason", comments);
            }
        } else if (taskId.startsWith("hodApprovalTask")) {
            variables.put("hodDecision", decision);
            if (comments != null && !comments.trim().isEmpty()) {
                variables.put("hodComments", comments);
            }
            if ("reject".equals(decision)) {
                variables.put("rejectionReason", comments);
            }
        } else if (taskId.startsWith("hrApprovalTask")) {
            variables.put("hrDecision", decision);
            if (comments != null && !comments.trim().isEmpty()) {
                variables.put("hrComments", comments);
            }
            if ("reject".equals(decision)) {
                variables.put("rejectionReason", comments);
            }
        }

        taskService.complete(taskId, variables);
    }
}