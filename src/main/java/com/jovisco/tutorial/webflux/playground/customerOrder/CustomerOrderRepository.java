package com.jovisco.tutorial.webflux.playground.customerOrder;

import com.jovisco.tutorial.webflux.playground.product.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerOrderRepository extends R2dbcRepository<CustomerOrder, UUID> {

    @Query("""
       SELECT
            p.*
        FROM
            customers c
        INNER JOIN customer_orders co ON c.id = co.customer_id
        INNER JOIN products p ON co.product_id = p.id
        WHERE
            c.name = :name
       """)
    Flux<Product> getProductsOrderedByCustomer(String name);

    @Query("""
        SELECT
            co.order_id,
            c.name AS customer_name,
            p.description AS product_name,
            co.amount,
            co.order_date
        FROM
            customers c
        INNER JOIN customer_orders co ON c.id = co.customer_id
        INNER JOIN products p ON co.product_id = p.id
        WHERE
            p.description = :description
        ORDER BY co.amount DESC
        """)
    Flux<CustomerOrderResponseDto> getOrderDetailsByProduct(String description);
}
