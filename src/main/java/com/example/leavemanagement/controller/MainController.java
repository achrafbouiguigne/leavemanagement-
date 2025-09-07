package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest.LeaveStatus;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.service.LeaveProcessService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveProcessService leaveProcessService;

    public MainController(UserRepository userRepository,
                          LeaveRequestRepository leaveRequestRepository,
                          LeaveProcessService leaveProcessService) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveProcessService = leaveProcessService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            // Check user roles to determine dashboard type
            if (user.getRoles().contains("HR")) {
                return "redirect:/hr/dashboard";
            } else if (user.getIsHod()) {
                return "redirect:/hod/dashboard";
            } else if (user.getManager() != null && user.getManager().getIsHod()) {
                return "redirect:/manager/dashboard";
            } else {
                return "redirect:/employee/dashboard";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}