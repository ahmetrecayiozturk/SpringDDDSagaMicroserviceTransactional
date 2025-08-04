package org.example.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    //eventleri string publish edelim ki tip karışıklıkları olmasın
    private KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void publishOutboxEvents() {
        //List<OutboxEvent> events = outboxEventRepository.findAll();
        //published false olanları burada filtreleyeceğimize direkt false olanalrı repository'den çekelim
        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalse();
        //Şimdi for ile dönüp bunları kafka template ile publish edeceğiz
        for(OutboxEvent event : events) {
            try{
                System.out.println("Publishing event to Kafka: "+event.getEventType()+" payload: "+event.getPayload());
                //önce verileri string halde publish edelim
                kafkaTemplate.send(event.getEventType(), event.getPayload());
                //sonra da publishedi true yapalım ki tekrardan publish etmeyelim burada
                event.setPublished(true);
                //ve tekrardan kaydedelim günceledikten sonra eventi
                outboxEventRepository.save(event);
            }
            catch (Exception e) {
                //Hata durumunda loglama yapabiliriz
                System.err.println("Failed to publish event: " + event.getId() + ", error: " + e.getMessage());
                throw e;
            }
        }
    }
    @Scheduled(fixedRate = 5000)
    public void logSomeThing() {
        System.out.println("5 saniyede bir loglama yapılıyor");
    }
}