package com.jovisco.tutorial.webflux.playground.customer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetAllCustomers() {
        webTestClient.get()
                .uri("/customers")
                .header("auth-token", "secret123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerResponseDto.class)
                .value(list -> log.info("{}", list))
                .hasSize(10);
    }

    @Test
    void testGetAllCustomersPaginated() {
        webTestClient.get()
                .uri("/customers/paginated?pageNo=2&pageSize=3")
                .header("auth-token", "secret123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(res -> log.info("{}", new String(res.getResponseBody())))
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("emily")
                .jsonPath("$[0].id").isEqualTo(4);
    }

    @Test
    void testGetCustomerById() {
        webTestClient.get()
                .uri("/customers/7")
                .header("auth-token", "secret123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(res -> log.info("{}", new String(res.getResponseBody())))
                .jsonPath("$.name").isEqualTo("olivia")
                .jsonPath("$.id").isEqualTo(7);
    }

    @Test
    void testGetCustomerByIdNotFound() {
        webTestClient.get()
                .uri("/customers/99")
                .header("auth-token", "secret123")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateAndDeleteCustomer() {
        var dto = new CustomerRequestDto("hansi", "hansi@horsti.de");

        webTestClient.post()
                .uri("/customers")
                .header("auth-token", "secret456")
                .bodyValue(dto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(res -> log.info("{}", new String(res.getResponseBody())))
                .jsonPath("$.id").isEqualTo(11)
                .jsonPath("$.name").isEqualTo("hansi")
                .jsonPath("$.email").isEqualTo("hansi@horsti.de");

        webTestClient.delete()
                .uri("/customers/11")
                .header("auth-token", "secret456")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void testUpdateCustomer() {
        var dto = new CustomerRequestDto("olivia", "olivia@horsti.de");

        webTestClient.put()
                .uri("/customers/7")
                .header("auth-token", "secret456")
                .bodyValue(dto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(res -> log.info("{}", new String(res.getResponseBody())))
                .jsonPath("$.id").isEqualTo(7)
                .jsonPath("$.name").isEqualTo("olivia")
                .jsonPath("$.email").isEqualTo("olivia@horsti.de");
    }

    @Test
    void testDeleteCustomerNotFound() {

        webTestClient.delete()
                .uri("/customers/99")
                .header("auth-token", "secret456")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateCustomerNotFound() {

        var dto = new CustomerRequestDto("not_found", "notfound@horsti.de");

        webTestClient.put()
                .uri("/customers/99")
                .header("auth-token", "secret456")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void testInvalidInput(){
        // missing name
        var missingName = new CustomerRequestDto( null, "hansi@horsti.de");
        this.webTestClient.post()
                .uri("/customers")
                .header("auth-token", "secret456")
                .bodyValue(missingName)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");

        // missing email
        var missingEmail = new CustomerRequestDto("hansi", null);
        this.webTestClient.post()
                .uri("/customers")
                .header("auth-token", "secret456")
                .bodyValue(missingEmail)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");

        // invalid email
        var invalidEmail = new CustomerRequestDto( "hansi", "hansi#horsti.de");
        this.webTestClient.put()
                .uri("/customers/10")
                .header("auth-token", "secret456")
                .bodyValue(invalidEmail)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    public void testUnauthorized(){
        var dto = new CustomerRequestDto( "hansi", "hansi@horsti.de");
        this.webTestClient.post()
                .uri("/customers")
                // .header("auth-token", "secret456")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testForbidden(){
        var dto = new CustomerRequestDto( "hansi", "hansi@horsti.de");
        this.webTestClient.post()
                .uri("/customers")
                .header("auth-token", "secret123")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }
}