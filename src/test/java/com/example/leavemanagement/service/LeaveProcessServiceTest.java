package com.example.leavemanagement.service;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LeaveProcessServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaveProcessService leaveProcessService;

    private User testUser;
    private LeaveRequest testLeaveRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testLeaveRequest = new LeaveRequest();
        testLeaveRequest.setId(1L);
        testLeaveRequest.setEmployee(testUser);
        testLeaveRequest.setReason("Test leave");
    }

    @Test
    public void testStartLeaveProcess() {
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        leaveProcessService.startLeaveProcess(testLeaveRequest);

        verify(leaveRequestRepository, times(2)).save(any(LeaveRequest.class));
        assertNotNull(testLeaveRequest.getProcessInstanceId());
    }
}