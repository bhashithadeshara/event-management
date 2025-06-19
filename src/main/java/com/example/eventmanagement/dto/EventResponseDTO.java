package com.example.eventmanagement.dto;

import com.example.eventmanagement.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private Event.Visibility visibility;
    private String hostName;
    private long attendeeCount;
}