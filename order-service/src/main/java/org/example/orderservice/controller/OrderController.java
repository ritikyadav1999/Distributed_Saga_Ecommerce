package org.example.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.enity.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Order create(@Valid @RequestBody CreateOrderRequest request,
                        @RequestHeader(value = "X-Idempotency-key",required = true) String key,
                        @RequestHeader(value = "X-Correlation-Id",required = false) String correlationId){
        System.out.println("Checkpoint:" + request.toString());
        if(correlationId == null || correlationId.isBlank()){
            correlationId = UUID.randomUUID().toString();
        }
        return orderService.createorder(request,key,correlationId);
    }


}
