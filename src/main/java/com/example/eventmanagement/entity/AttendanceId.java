package com.example.eventmanagement.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceId implements Serializable {
    private UUID event;
    private UUID user;
}
