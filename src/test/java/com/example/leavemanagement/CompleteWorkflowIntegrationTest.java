package com.example.leavemanagement;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest.LeaveStatus  ;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.service.LeaveProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CompleteWorkflowIntegrationTest {

    @Autowired
    private LeaveProcessService leaveProcessService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // Create test user
        testUser = userRepository.findByUsername("testemployee")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("testemployee");
                    user.setPassword("password");
                    user.setFirstName("Test");
                    user.setLastName("Employee");
                    user.setEmail("test@company.com");
                    user.setLeaveBalance(20);
                    return userRepository.save(user);
                });
    }

    @Test
    @Rollback(false)
    public void testBasicWorkflow() {
        System.out.println("=== Testing Basic Workflow ===");

        // Simple test to verify basic functionality
        assertNotNull(leaveProcessService);
        assertNotNull(leaveRequestRepository);
        assertNotNull(userRepository);

        // Test user creation
        assertNotNull(testUser);
        assertEquals("testemployee", testUser.getUsername());

        System.out.println("✓ Basic test passed");
    }

    @Test
    @Rollback(false)
    public void testLeaveRequestCreation() {
        System.out.println("=== Testing Leave Request Creation ===");

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(testUser);
        leaveRequest.setReason("Test vacation");
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(5));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        System.out.println("✓ Leave request created: " + leaveRequest.getId());

        // Verify it was saved
        Optional<LeaveRequest> foundRequest = leaveRequestRepository.findById(leaveRequest.getId());
        assertTrue(foundRequest.isPresent());
        assertEquals("Test vacation", foundRequest.get().getReason());

        System.out.println("✓ Leave request verification passed");
    }
}