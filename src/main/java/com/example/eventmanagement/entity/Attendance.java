package com.example.eventmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "attendance")
@IdClass(AttendanceId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant respondedAt;

    public enum Status {
        GOING,
        MAYBE,
        DECLINED
    }
}
