package com.example.eventmanagement.repository;

import com.example.eventmanagement.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByArchivedFalseAndStartTimeBetweenAndLocationAndVisibility(
            Instant start, Instant end, String location, Event.Visibility visibility);

    Page<Event> findByArchivedFalseAndStartTimeAfter(Instant startTime, Pageable pageable);

    List<Event> findByArchivedFalseAndHostId(UUID hostId);
}
