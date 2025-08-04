package org.example.api;

import com.sun.istack.NotNull;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.domain.model.OrderStatus;

//burada order oluşturma isteği için gerekli olan alanları tanımlıyorum
@Data
public class OrderCreateRequest {
    @NotNull
    private String customerName;
    @NotNull
    private String productName;
}
