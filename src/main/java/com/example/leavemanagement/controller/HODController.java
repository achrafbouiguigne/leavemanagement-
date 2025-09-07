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
@RequestMapping("/hod")
public class HODController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveProcessService leaveProcessService;

    public HODController(UserRepository userRepository,
                         LeaveRequestRepository leaveRequestRepository,
                         LeaveProcessService leaveProcessService) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveProcessService = leaveProcessService;
    }

    @GetMapping("/dashboard")
    public String hodDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User hod = userOptional.get();
            model.addAttribute("user", hod);

            // Get escalated leave requests
            List<LeaveRequest> escalatedRequests = leaveRequestRepository.findEscalatedRequests(LeaveStatus.ESCALATED);
            model.addAttribute("escalatedRequests", escalatedRequests);

            // Get tasks for the HOD
            List<Task> tasks = leaveProcessService.getTasksForUser(hod.getUsername());
            model.addAttribute("tasks", tasks);

            return "hod/dashboard";
        }

        return "redirect:/login";
    }

    @PostMapping("/approve/{taskId}")
    public String approveLeave(@PathVariable String taskId,
                               @RequestParam(required = false) String comments) {
        leaveProcessService.completeTask(taskId, "approve", comments);
        return "redirect:/hod/dashboard";
    }

    @PostMapping("/reject/{taskId}")
    public String rejectLeave(@PathVariable String taskId,
                              @RequestParam String rejectionReason) {
        leaveProcessService.completeTask(taskId, "reject", rejectionReason);
        return "redirect:/hod/dashboard";
    }
}