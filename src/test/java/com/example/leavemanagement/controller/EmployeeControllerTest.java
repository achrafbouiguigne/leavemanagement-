package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.service.LeaveProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveProcessService leaveProcessService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Model model;

    @InjectMocks
    private EmployeeController employeeController;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setLeaveBalance(20);

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    public void  testEmployeeDashboard() {
        when(leaveRequestRepository.findByEmployee(testUser))
                .thenReturn(Arrays.asList(new LeaveRequest(), new LeaveRequest()));

        String viewName = employeeController.employeeDashboard(userDetails, model);

        assertEquals("employee/dashboard", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
        verify(model, times(1)).addAttribute(eq("leaveRequests"), anyList());
    }
}