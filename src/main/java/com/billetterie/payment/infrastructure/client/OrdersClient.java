package com.billetterie.payment.infrastructure.client;

import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrdersClient implements Orders {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersClient.class);

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
            LOGGER.debug("Transforming cart {} to order with amount {} with request {}", cartId, amount, request);
            ResponseEntity<OrderResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    OrderResponse.class
            );

            return response.getBody().toOrder();
        } catch (Exception e) {
            LOGGER.error("Error while transforming to order with request {}", request, e);
            return null;
        }
    }
}
