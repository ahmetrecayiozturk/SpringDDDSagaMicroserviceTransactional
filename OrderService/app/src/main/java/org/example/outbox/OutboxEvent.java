package org.example.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventType;
    private String payload;
    private Long createdtime = System.currentTimeMillis();
    private boolean published = false;
    public OutboxEvent( String eventType, String payload) {
        // Default constructor for JPA
        this.eventType = eventType;
        this.payload = payload;
    }

    public OutboxEvent() {

    }
}
