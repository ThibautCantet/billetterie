package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CartType;
import com.billetterie.payment.domain.ClassicOrderCreated;
import com.billetterie.payment.domain.ClassicOrderNotCreated;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PanierReserveCreated;
import com.billetterie.payment.domain.PanierReserveNotCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrderCommandHandler implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrderCommandHandler.class);

    private final Orders orders;

    public TransformToOrderCommandHandler(Orders orders) {
        this.orders = orders;
    }

    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());

            if (command.cartType() == CartType.CLASSIC) {
                LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", command.cartId());

                var failed = ClassicOrderNotCreated.of(command.transactionId(), command.amount(), command.cartId());
                return new CommandResponse<>(failed);
            } else {
                LOGGER.info("Panier reservé not transformed into order and redirect error: {}", command.cartId());

                var failed = PanierReserveNotCreated.of(command.transactionId(), command.amount(), command.cartId());
                return new CommandResponse<>(failed);
            }
        }

        if (command.cartType() == CartType.CLASSIC) {
            LOGGER.info("Cart transformed to order: {}", order.id());
            var classicOrderCreated = ClassicOrderCreated.of(command, order.id());

            return new CommandResponse<>(classicOrderCreated);
        } else {
            LOGGER.info("Panier réservé transformed to order: {}", order.id());
            var panierReserveCreated = PanierReserveCreated.of(command, order.id());

            return new CommandResponse<>(panierReserveCreated);
        }
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }

    public static String getErrorUrl(String cartId, float amount) {
        return "/panier-reserve-error?cartId=" + cartId + "&amount=" + amount;
    }
}
