package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.EventRequestDTO;
import com.example.eventmanagement.dto.EventResponseDTO;
import com.example.eventmanagement.entity.Event;
import com.example.eventmanagement.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.eventmanagement.mapper.EventManualMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eventmanagement.service.EventService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventManualMapper eventMapper;

    // Simulated current user retrieval
    private User getCurrentUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .role(User.Role.USER)
                .build();
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO dto) {
        User user = getCurrentUser();
        Event event = eventMapper.toEntity(dto);
        event.setHost(user);
        Event saved = eventService.createEvent(event);
        return ResponseEntity.ok(eventMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable UUID id, @Valid @RequestBody EventRequestDTO dto) {
        User user = getCurrentUser();
        Event updatedEvent = eventMapper.toEntity(dto);
        return eventService.updateEvent(id, updatedEvent, user)
                .map(eventMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        User user = getCurrentUser();
        eventService.deleteEvent(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventResponseDTO>> filterEvents(
            @RequestParam Instant start,
            @RequestParam Instant end,
            @RequestParam String location,
            @RequestParam Event.Visibility visibility) {

        List<Event> events = eventService.filterEvents(start, end, location, visibility);
        List<EventResponseDTO> dtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<Event>> getUpcoming(Pageable pageable) {
        return ResponseEntity.ok(eventService.getUpcomingEvents(pageable));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventStatus(id));
    }

    @GetMapping("/user")
    public ResponseEntity<List<EventResponseDTO>> getUserEvents() {
        User user = getCurrentUser();
        List<Event> events = eventService.getEventsByUser(user.getId());
        List<EventResponseDTO> dtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getDetails(@PathVariable UUID id) {
        return eventService.getEventDetails(id)
                .map(eventMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
