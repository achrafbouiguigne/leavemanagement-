package com.example.leavemanagement.repository;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(User employee);

    // Use LeaveRequest.LeaveStatus instead of LeaveStatus
    List<LeaveRequest> findByEmployeeAndStatus(User employee, LeaveRequest.LeaveStatus status);
    List<LeaveRequest> findByStatus(LeaveRequest.LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.manager = :manager AND lr.status = :status")
    List<LeaveRequest> findByManagerAndStatus(@Param("manager") User manager,
                                              @Param("status") LeaveRequest.LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee IN " +
            "(SELECT u FROM User u WHERE u.isHod = true OR u.manager.isHod = true) " +
            "AND lr.status = :status")
    List<LeaveRequest> findEscalatedRequests(@Param("status") LeaveRequest.LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED' " +
            "AND lr.startDate <= :endDate AND lr.endDate >= :startDate")
    List<LeaveRequest> findApprovedLeavesInRange(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    List<LeaveRequest> findByProcessInstanceId(String processInstanceId);
}
