package org.example.kafka_consumer.order;

import lombok.extern.slf4j.Slf4j;
import org.example.Json.JsonUtil;
import org.example.event.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//burada ne yapıyoruz, burası localde çalışan kafka serverindaki eventleri dinleyecek ve eğer gelen eventleri topic'lerine göre yakalayacak ve ilgili topik
//methodunda ne varsa onu yapacak
@Slf4j//bu loglama için kullanılıyor
@Component
public class NotificationEventConsumer {
    //kafka listener kullanıyoruz bu içine yazdığımız topiğe göre otomatik olarak eventleri yakalıyor
    @KafkaListener(topics = "OrderNotificationTopic", groupId = "notification-service")
    public void consumeOrderCreatedEvent(String eventJson){
        try{
            //Önce gelen json'u evente çeviriyoruz
            NotificationEvent notificationEvent = JsonUtil.objectMapper().readValue(eventJson, NotificationEvent.class);
            //Sonra da bu eventi logluyoruz
            log.info("Order Created Event Notification received: {}", notificationEvent);
            //print te edelim ne olur ne olmaz
            System.out.println("Order Created Event Notification received: " + notificationEvent);
        }
        catch(Exception e){
            //eğer bir hata gerçekleşirse hem loglayalım hem de print edelim
            log.error("Error consuming NotificationEvent: {}", eventJson, e);
            System.err.println("Error consuming NotificationEvent: " + eventJson + ", Error: " + e.getMessage());
        }
    }
}
