package com.example.eventmanagement.service;

import com.example.eventmanagement.entity.Event;
import com.example.eventmanagement.entity.User;
import com.example.eventmanagement.repository.AttendanceRepository;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private AttendanceRepository attendanceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User hostUser;
    private Event event;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        hostUser = User.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .email("alice@example.com")
                .role(User.Role.USER)
                .build();

        event = Event.builder()
                .id(UUID.randomUUID())
                .title("Test Event")
                .description("Desc")
                .location("Colombo")
                .startTime(Instant.now().plusSeconds(3600))
                .endTime(Instant.now().plusSeconds(7200))
                .visibility(Event.Visibility.PUBLIC)
                .host(hostUser)
                .archived(false)
                .build();
    }

    @Test
    void testCreateEvent_ShouldSetArchivedFalseAndSave() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.createEvent(event);

        assertNotNull(result);
        assertFalse(result.isArchived());
        verify(eventRepository).save(event);
    }

    @Test
    void testUpdateEvent_ByHost_ShouldUpdateFields() {
        Event updated = event.toBuilder()
                .title("Updated")
                .description("Updated desc")
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(updated);

        Optional<Event> result = eventService.updateEvent(event.getId(), updated, hostUser);

        assertTrue(result.isPresent());
        assertEquals("Updated", result.get().getTitle());
    }

    @Test
    void testUpdateEvent_ByUnauthorizedUser_ShouldThrowSecurityException() {
        User other = User.builder().id(UUID.randomUUID()).role(User.Role.USER).build();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(SecurityException.class, () ->
                eventService.updateEvent(event.getId(), event, other));
    }

    @Test
    void testDeleteEvent_ShouldSoftDelete() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.deleteEvent(event.getId(), hostUser);

        assertTrue(event.isArchived());
        verify(eventRepository).save(event);
    }

    @Test
    void testGetEventStatus_Upcoming() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        String status = eventService.getEventStatus(event.getId());

        assertEquals("UPCOMING", status);
    }

    @Test
    void testGetEventDetails_ShouldReturnIfNotArchived() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventDetails(event.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void testGetEventDetails_Archived_ShouldReturnEmpty() {
        event.setArchived(true);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventDetails(event.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUpcomingEvents_ShouldReturnPage() {
        Page<Event> page = new PageImpl<>(List.of(event));
        when(eventRepository.findByArchivedFalseAndStartTimeAfter(any(), any())).thenReturn(page);

        Page<Event> result = eventService.getUpcomingEvents(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

}
