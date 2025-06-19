package com.example.eventmanagement.repository;

import com.example.eventmanagement.entity.Attendance;
import com.example.eventmanagement.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
    List<Attendance> findByUserId(UUID userId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.event.id = :eventId")
    long countAttendeesByEventId(UUID eventId);

}
