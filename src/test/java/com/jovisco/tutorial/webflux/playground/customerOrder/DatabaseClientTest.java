package com.jovisco.tutorial.webflux.playground.customerOrder;

import com.jovisco.tutorial.webflux.playground.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class DatabaseClientTest extends AbstractTest {

    @Autowired
    private DatabaseClient client;

    @Test
    public void testSelectOrderDetailsByProduct() {
        var query = """
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
            """;

        client.sql(query)
                .bind("description", "iphone 20")
                .mapProperties(CustomerOrderResponseDto.class)
                .all()
                .doOnNext(dto -> log.info("{}", dto))
                .as(StepVerifier::create)
                .assertNext(dto -> assertEquals(975, dto.amount()))
                .assertNext(dto -> assertEquals(950, dto.amount()))
                .expectComplete()
                .verify();
    }
}
