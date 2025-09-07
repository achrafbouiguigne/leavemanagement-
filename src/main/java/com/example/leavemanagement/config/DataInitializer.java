package com.example.leavemanagement.config;

import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@company.com");
            admin.setRoles(Arrays.asList("ADMIN", "HR", "MANAGER"));
            admin.setIsHr(true);
            userRepository.save(admin);
        }

        // Create HR user
        if (userRepository.findByUsername("hr").isEmpty()) {
            User hr = new User();
            hr.setUsername("hr");
            hr.setPassword(passwordEncoder.encode("hr123"));
            hr.setFirstName("HR");
            hr.setLastName("Manager");
            hr.setEmail("hr@company.com");
            hr.setRoles(Arrays.asList("HR"));
            hr.setIsHr(true);
            userRepository.save(hr);
        }

        // Create HOD user
        if (userRepository.findByUsername("hod").isEmpty()) {
            User hod = new User();
            hod.setUsername("hod");
            hod.setPassword(passwordEncoder.encode("hod123"));
            hod.setFirstName("Head");
            hod.setLastName("Of Department");
            hod.setEmail("hod@company.com");
            hod.setRoles(Arrays.asList("HOD", "MANAGER"));
            hod.setIsHod(true);
            userRepository.save(hod);
        }

        // Create manager user
        if (userRepository.findByUsername("manager").isEmpty()) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Department");
            manager.setLastName("Manager");
            manager.setEmail("manager@company.com");
            manager.setRoles(Arrays.asList("MANAGER"));

            // Set HOD as manager's manager
            User hod = userRepository.findByUsername("hod").orElse(null);
            manager.setManager(hod);

            userRepository.save(manager);
        }

        // Create employee user
        if (userRepository.findByUsername("employee").isEmpty()) {
            User employee = new User();
            employee.setUsername("employee");
            employee.setPassword(passwordEncoder.encode("employee123"));
            employee.setFirstName("John");
            employee.setLastName("Doe");
            employee.setEmail("employee@company.com");
            employee.setRoles(Arrays.asList("EMPLOYEE"));

            // Set manager as employee's manager
            User manager = userRepository.findByUsername("manager").orElse(null);
            employee.setManager(manager);

            userRepository.save(employee);
        }
    }
}