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
@RequestMapping("/hr")
public class HRController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveProcessService leaveProcessService;

    public HRController(UserRepository userRepository,
                        LeaveRequestRepository leaveRequestRepository,
                        LeaveProcessService leaveProcessService) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveProcessService = leaveProcessService;
    }

    @GetMapping("/dashboard")
    public String hrDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User hr = userOptional.get();
            model.addAttribute("user", hr);

            // Get all pending leave requests
            List<LeaveRequest> pendingRequests = leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
            model.addAttribute("pendingRequests", pendingRequests);

            // Get tasks for HR
            List<Task> tasks = leaveProcessService.getTasksForGroup("HR");
            model.addAttribute("tasks", tasks);

            return "hr/dashboard";
        }

        return "redirect:/login";
    }

    @PostMapping("/approve/{taskId}")
    public String approveLeave(@PathVariable String taskId,
                               @RequestParam(required = false) String comments) {
        leaveProcessService.completeTask(taskId, "approve", comments);
        return "redirect:/hr/dashboard";
    }

    @PostMapping("/reject/{taskId}")
    public String rejectLeave(@PathVariable String taskId,
                              @RequestParam String rejectionReason) {
        leaveProcessService.completeTask(taskId, "reject", rejectionReason);
        return "redirect:/hr/dashboard";
    }

    @GetMapping("/payroll")
    public String payrollView(Model model) {
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED);
        model.addAttribute("approvedLeaves", approvedLeaves);
        return "hr/payroll";
    }
}