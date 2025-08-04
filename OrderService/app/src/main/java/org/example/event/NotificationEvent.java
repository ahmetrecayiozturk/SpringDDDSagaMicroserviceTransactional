package org.example.event;

import lombok.Data;

@Data
public class NotificationEvent {
    private Long orderId;
    private String customerName;
    private String productName;
    private String message;
}
