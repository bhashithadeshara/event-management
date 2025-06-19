package com.example.eventmanagement.mapper;

import com.example.eventmanagement.dto.EventRequestDTO;
import com.example.eventmanagement.dto.EventResponseDTO;
import com.example.eventmanagement.entity.Event;
import com.example.eventmanagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventManualMapper {

    private final EventService eventService;

    // DTO -> Entity
    public Event toEntity(EventRequestDTO dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());
        event.setVisibility(dto.getVisibility());
        return event;
    }

    // Entity -> DTO
    public EventResponseDTO toDto(Event event) {
        if (event == null) return null;
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setLocation(event.getLocation());
        dto.setVisibility(event.getVisibility());
        dto.setHostName(event.getHost() != null ? event.getHost().getName() : null);
        dto.setAttendeeCount(eventService.getAttendeeCount(event.getId()));
        return dto;
    }
}
