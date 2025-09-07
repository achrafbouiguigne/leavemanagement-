package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest.LeaveStatus;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.service.LeaveProcessService;
import org.flowable.task.api.Task;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveProcessService leaveProcessService;

    public ManagerController(UserRepository userRepository,
                             LeaveRequestRepository leaveRequestRepository,
                             LeaveProcessService leaveProcessService) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveProcessService = leaveProcessService;
    }

    @GetMapping("/dashboard")
    public String managerDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                   @RequestParam(defaultValue = "pending") String filter) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User manager = userOptional.get();
            model.addAttribute("user", manager);

            List<LeaveRequest> leaveRequests;

            switch (filter) {
                case "completed":
                    leaveRequests = leaveRequestRepository.findByManagerAndStatus(manager, LeaveStatus.APPROVED);
                    leaveRequests.addAll(leaveRequestRepository.findByManagerAndStatus(manager, LeaveStatus.REJECTED));
                    break;
                case "escalated":
                    leaveRequests = leaveRequestRepository.findByManagerAndStatus(manager, LeaveStatus.ESCALATED);
                    break;
                case "pending":
                default:
                    leaveRequests = leaveRequestRepository.findByManagerAndStatus(manager, LeaveStatus.PENDING);
                    break;
            }

            model.addAttribute("leaveRequests", leaveRequests);
            model.addAttribute("filter", filter);

            // Get tasks for the manager
            List<Task> tasks = leaveProcessService.getTasksForUser(manager.getUsername());
            model.addAttribute("tasks", tasks);

            return "manager/dashboard";
        }

        return "redirect:/login";
    }

    @PostMapping("/approve/{taskId}")
    public String approveLeave(@PathVariable String taskId,
                               @RequestParam(required = false) String comments) {
        leaveProcessService.completeTask(taskId, "approve", comments);
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/reject/{taskId}")
    public String rejectLeave(@PathVariable String taskId,
                              @RequestParam String rejectionReason) {
        leaveProcessService.completeTask(taskId, "reject", rejectionReason);
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/escalate/{taskId}")
    public String escalateLeave(@PathVariable String taskId,
                                @RequestParam(required = false) String comments) {
        leaveProcessService.completeTask(taskId, "escalate", comments);
        return "redirect:/manager/dashboard";
    }
}