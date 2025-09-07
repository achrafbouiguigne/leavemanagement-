package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest.LeaveStatus;

import com.example.leavemanagement.entity.LeaveRequest.LeaveType;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.service.LeaveProcessService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveProcessService leaveProcessService;

    public EmployeeController(UserRepository userRepository,
                              LeaveRequestRepository leaveRequestRepository,
                              LeaveProcessService leaveProcessService) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveProcessService = leaveProcessService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployee(user);
            model.addAttribute("leaveRequests", leaveRequests);

            List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByEmployeeAndStatus(user, LeaveStatus.APPROVED);
            model.addAttribute("approvedLeaves", approvedLeaves);

            return "employee/dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/request")
    public String showLeaveRequestForm(Model model) {
        model.addAttribute("leaveTypes", LeaveType.values());
        model.addAttribute("leaveRequest", new LeaveRequest());
        return "employee/request-form";
    }

    @PostMapping("/request")
    public String submitLeaveRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam LeaveType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason) {

        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Create leave request
            LeaveRequest leaveRequest = new LeaveRequest(user, type, startDate, endDate, reason);
            leaveRequest = leaveRequestRepository.save(leaveRequest);

            // Start the process instance
            leaveProcessService.startLeaveProcess(leaveRequest);

            return "redirect:/employee/dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/calendar")
    public String showCalendar(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            // Get current month's approved leaves
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            List<LeaveRequest> approvedLeaves = leaveRequestRepository.findApprovedLeavesInRange(firstDayOfMonth, lastDayOfMonth);
            model.addAttribute("approvedLeaves", approvedLeaves);

            return "employee/calendar";
        }

        return "redirect:/login";
    }
}