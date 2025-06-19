package com.example.eventmanagement.service;

import com.example.eventmanagement.entity.Attendance;
import com.example.eventmanagement.entity.Event;
import com.example.eventmanagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.eventmanagement.repository.AttendanceRepository;
import com.example.eventmanagement.repository.EventRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;

    public Event createEvent(Event event) {
        event.setArchived(false); // ensure new events are active
        return eventRepository.save(event);
    }

    public Optional<Event> updateEvent(UUID id, Event updatedEvent, User currentUser) {
        return eventRepository.findById(id).filter(e -> !e.isArchived()).map(event -> {
            if (!event.getHost().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
                throw new SecurityException("Unauthorized");
            }
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setStartTime(updatedEvent.getStartTime());
            event.setEndTime(updatedEvent.getEndTime());
            event.setLocation(updatedEvent.getLocation());
            event.setVisibility(updatedEvent.getVisibility());
            return eventRepository.save(event);
        });
    }

    public void deleteEvent(UUID eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getHost().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new SecurityException("Unauthorized");
        }

        event.setArchived(true); // Soft delete: mark as archived
        eventRepository.save(event);
    }

    public List<Event> filterEvents(Instant start, Instant end, String location, Event.Visibility visibility) {
        return eventRepository.findByArchivedFalseAndStartTimeBetweenAndLocationAndVisibility(
                start, end, location, visibility);
    }

    public Page<Event> getUpcomingEvents(Pageable pageable) {
        return eventRepository.findByArchivedFalseAndStartTimeAfter(Instant.now(), pageable);
    }

    public List<Event> getEventsByUser(UUID userId) {
        List<Event> hosted = eventRepository.findByArchivedFalseAndHostId(userId);
        List<Attendance> attending = attendanceRepository.findByUserId(userId);
        List<Event> attendingEvents = attending.stream()
                .map(Attendance::getEvent)
                .filter(event -> !event.isArchived()) // filter archived
                .toList();
        hosted.addAll(attendingEvents);
        return hosted;
    }

    public Optional<Event> getEventDetails(UUID eventId) {
        return eventRepository.findById(eventId)
                .filter(event -> !event.isArchived());
    }

    public long getAttendeeCount(UUID eventId) {
        return attendanceRepository.countAttendeesByEventId(eventId);
    }

    public String getEventStatus(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .filter(e -> !e.isArchived())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Instant now = Instant.now();
        if (now.isBefore(event.getStartTime())) return "UPCOMING";
        if (now.isAfter(event.getEndTime())) return "COMPLETED";
        return "ONGOING";
    }
}
