package com.example.eventmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private String location;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private boolean archived = false;

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }
}
