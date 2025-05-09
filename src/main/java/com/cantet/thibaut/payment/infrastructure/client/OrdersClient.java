package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrdersClient implements Orders {

    @Value(value = "${orders.url}")
    private String ordersUrl;

    private final RestTemplate restTemplate;

    public OrdersClient() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Order transformToOrder(String cartId, float amount) {
        String url = ordersUrl + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> request = new HttpEntity<>(new OrderRequest(cartId, amount), headers);

        try {
            ResponseEntity<OrderResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    OrderResponse.class
            );

            return response.getBody().toOrder();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
