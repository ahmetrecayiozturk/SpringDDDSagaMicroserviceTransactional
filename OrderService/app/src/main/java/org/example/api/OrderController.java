package org.example.api;

import org.example.application.OrderSagaOrchestrator;
import org.example.infrastructure.OrderRepository;
import org.example.domain.aggregate.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

//burada aslında biz bir servis kullanmıyoruz normalde olduğu gibi, saga patterni ile bunu tek bir yerden(orkestrasyon) ile yönetiyoruz
//saga içindeki transactional metotlar ile sorun olmadığı durumlarda eventleri outbox olarak kaydedip sonra da onları scheculed anatasyonu ile
//belli süreli aralıklar ile published=false olanları publish ediyoruz, burada iki adet event yayınlıyoruz, birincisi ordercreatedevent,
//ikincisi ise notificationevent, ordercreatedevent bu order service içinde dinleniyor, notificataionevent ise başka yerden dinleniyor,
//ordercreatedevent eğer kafka listener tarafından alınırsa biz order'ın artık oluşturulduğunu anlıyoruz ve order'i setStatus(OrderStatus.COMPLETED)
//yapıyoruz, yani order artık tam olarak kaydedildikten sonra completed diyoruz default olaraksa pending olarak kaydediyorum orderimi
@RestController
@RequestMapping("/orders")
public class OrderController {
    //order repository'i sadece get işlemi için kullanacağım burada
    private final OrderRepository orderRepository;
    //order saga orchestratorı ise order oluşturma işlemi için kullanacağım
    private final OrderSagaOrchestrator orderSagaOrchestrator;
    public OrderController(OrderRepository orderRepository, OrderSagaOrchestrator orderSagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.orderSagaOrchestrator = orderSagaOrchestrator;
    }

    //burada orderSagaOrchestrator ile order oluşturma işlemini başlatıyorum
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderCreateRequest orderCreateRequest) throws IOException {
        orderSagaOrchestrator.startNewOrderSaga(orderCreateRequest);
        return ResponseEntity.ok("Order Creation Process Started");
    }
    @GetMapping("/created-orders")
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
}
