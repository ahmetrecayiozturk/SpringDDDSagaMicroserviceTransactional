package org.example.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private String customerName;
    private String productName;
}
