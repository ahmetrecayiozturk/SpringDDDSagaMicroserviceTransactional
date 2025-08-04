package org.example.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.OrderCreateRequest;
import org.example.domain.aggregate.Order;
import org.example.domain.model.OrderStatus;
import org.example.event.NotificationEvent;
import org.example.event.OrderCreatedEvent;
import org.example.infrastructure.OrderRepository;
import org.example.outbox.OutboxEvent;
import org.example.outbox.OutboxEventPublisher;
import org.example.outbox.OutboxEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class OrderSagaOrchestrator {
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public OrderSagaOrchestrator(OrderRepository orderRepository, OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //burası önce transactional olacak, order save olursa event oluşturulup save edilecek, transactional işlem gerçekleştiğinde eğer rollback olunmamışsa yani hata alınmamışsa
    //biz save ettiğimiz eventi outboxeventpublisher den publish edeceğiz, parametreleri order nesnesine ait olmalı yani, burada biz order controller'da ordercreatedeventi direkt buraya da
    //versek yeter aslında
    @Transactional
    public void startNewOrderSaga(OrderCreateRequest orderCreateRequest) throws IOException {
        Order order = new Order();
        order.setProductName(orderCreateRequest.getProductName());
        order.setCustomerName(orderCreateRequest.getCustomerName());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        //Şimdi transactional olduğu için şöyle bi şey olacak, benim eventim burada ortada outboxevent dediğim yere kaydolacak, bu neyi sağlıyor bize, şunu sağlıyor
        //eğer orderı kaydederken bir hata alırsam transactional zincir bozulacağından db'lere herhangi bir commit olmayacağından,outboxevente de herhangi bir
        // event kaydedilmediği için event de publish edilmeyecek, yani böyle yapmamızın sebebi kesin olarak order kaydedilirse eventi publish etmemiz


        //şimdi burada save edildiyse eğer orderimiz transactionalda bir sorun olmamış yani db'ye commit edilmiş bilgiler demektir o yüzden burada outobox'a eventimi oluşturup
        //kaydedelim sonra da @Scheculed anatasyonu ile belli aralıklar ile bu eventleri publish edeceğiz
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(order.getId());
        orderCreatedEvent.setCustomerName(order.getCustomerName());
        orderCreatedEvent.setProductName(order.getProductName());
        //şimdi burada object mapper ile stringe çeviricez payload olarka vericez bi de eventype yazıp kaydedicez bakalım
        String payload = objectMapper.writeValueAsString(orderCreatedEvent);
        OutboxEvent outboxEvent = new OutboxEvent("OrderCreatedEvent", payload);
        outboxEventRepository.save(outboxEvent);
    }
    //order create olduktan sonra bunu publish edeceğiz ama önce order'in statusunu completed yapalım ve sonra da yayınlayacağız yayınlama burdan deil zaten yani başka bir yerde olacak
    @KafkaListener(topics ="OrderCreatedEvent", groupId = "order-saga")
    public void handleOrderCreatedEvent(String payload) {
        try {
            System.out.println("abi şuan ordercreatedevent yakalanmış gözüküyor"+objectMapper.readValue(payload, OrderCreatedEvent.class));
            OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload, OrderCreatedEvent.class);
            //şimdi bu eventi kullanarak order'ı güncelleyelim, orderi id ile bulalım sonra da completed yapalım
            Order order = orderRepository.findById(orderCreatedEvent.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(OrderStatus.COMPLETED);
            //değişiklikleri kaydedelim
            orderRepository.save(order);
            //şimdi notification servicem için notificationMessage eventi publish edicem
            NotificationEvent notificationEvent = new NotificationEvent();
            notificationEvent.setOrderId(order.getId());
            notificationEvent.setCustomerName(order.getCustomerName());
            notificationEvent.setProductName(order.getProductName());
            String message = "Your order with ID " + order.getId() + "and that customer name: " + order.getCustomerName()+ "and that product name"+ order.getProductName() +  " has been completed successfully.";
            notificationEvent.setMessage(message);
            String notificationMessage = "Order with ID " + order.getId() + " has been completed.";
            //mesajı string olarak publish edeceğimizden önce objectmapper ile stringe çevirelim
            String notificationPayload = objectMapper.writeValueAsString(notificationEvent);
            kafkaTemplate.send("OrderNotificationTopic", notificationPayload);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
