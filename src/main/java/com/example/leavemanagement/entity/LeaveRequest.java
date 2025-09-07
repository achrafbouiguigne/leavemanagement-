package com.example.leavemanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "approved_by_manager")
    private Boolean approvedByManager;

    @Column(name = "approved_by_hod")
    private Boolean approvedByHod;

    @Column(name = "approved_by_hr")
    private Boolean approvedByHr;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "medical_document_path")
    private String medicalDocumentPath;

    @Column(name = "payroll_updated")
    private Boolean payrollUpdated = false;

    // Constructors
    public LeaveRequest() {}

    public LeaveRequest(User employee, LeaveType type, LocalDate startDate,
                        LocalDate endDate, String reason) {
        this.employee = employee;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public LeaveType getType() { return type; }
    public void setType(LeaveType type) { this.type = type; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getApprovedByManager() { return approvedByManager; }
    public void setApprovedByManager(Boolean approvedByManager) { this.approvedByManager = approvedByManager; }

    public Boolean getApprovedByHod() { return approvedByHod; }
    public void setApprovedByHod(Boolean approvedByHod) { this.approvedByHod = approvedByHod; }

    public Boolean getApprovedByHr() { return approvedByHr; }
    public void setApprovedByHr(Boolean approvedByHr) { this.approvedByHr = approvedByHr; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getMedicalDocumentPath() { return medicalDocumentPath; }
    public void setMedicalDocumentPath(String medicalDocumentPath) { this.medicalDocumentPath = medicalDocumentPath; }

    public Boolean getPayrollUpdated() { return payrollUpdated; }
    public void setPayrollUpdated(Boolean payrollUpdated) { this.payrollUpdated = payrollUpdated; }

    // Duration helper
    public long getDuration() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    // Nested enums
    public enum LeaveType {
        VACATION, SICK, EMERGENCY
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, ESCALATED, CANCELLED
    }
}
