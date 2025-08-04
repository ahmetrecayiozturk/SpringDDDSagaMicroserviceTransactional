package org.example.domain.aggregate;

import jakarta.persistence.*;
import lombok.Data;
import org.example.domain.model.OrderStatus;

//@Builder builderi kullanmak bana bazen hata çıkarıyor önce normal yapıcam sonra bakıcam buna
@Data
@Entity
@Table(name = "order_aggregate")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    private String productName;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING ;//default olarak PENDING ayarlıyoruz
}
