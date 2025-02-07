package com.jovisco.tutorial.webflux.playground;

import com.jovisco.tutorial.webflux.playground.product.ProductRequestDto;
import com.jovisco.tutorial.webflux.playground.product.ProductResponseDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class WebClientTest extends AbstractWebClient {

    record CalculatorResponseDto(Integer first, Integer second, String operation, Integer result) {};

    private final WebClient webClient = createWebClient(
            builder -> builder.filter(logMethodAndUri()));

    public WebClientTest() {
        super();
    }

    @Test
    public void testGetProductById() {
        webClient.get()
                .uri("/lec01/product/1")
                .attribute("enable-logging", false)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(1, p.id()))
                .expectComplete()
                .verify();

    }

    @SneakyThrows
    @Test
    public void testConcurrentRequests() {
        for (int i = 1; i < 10; i++) {
            webClient.get()
                    .uri("/lec01/product/{id}", i)
                    .retrieve()
                    .bodyToMono(ProductResponseDto.class)
                    .doOnNext(p -> log.info("{}", p))
                    .subscribe();
        }
        Thread.sleep(2000L);
    }

    @Test
    public void testGetProductStream() {
        webClient.get()
                .uri("/lec02/product/stream")
                .retrieve()
                .bodyToFlux(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
//                .take(Duration.ofSeconds(2L))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

    }

    @Test
    public void testCreateProductUsingBodyValue() {
        webClient.post()
                .uri("/lec03/product")
                .bodyValue(new ProductRequestDto( "Hansi-Poster", 1234))
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testCreateProductUsingBody() {
        var mono = Mono.fromSupplier(() -> new ProductRequestDto( "Willibild", 43))
                        .delayElement(Duration.ofMillis(500L));

        webClient.post()
                .uri("/lec03/product")
                // use body instead of bodyValue
                .body(mono, ProductRequestDto.class)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testGetProductByIdWithDefaultHeader() {
        var webClientWithDefaultHeader = createWebClient(
                builder -> builder.defaultHeader("caller-id", "order-service")
        );
        webClientWithDefaultHeader.get()
                .uri("/lec04/product/{id}", 1)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(1, p.id()))
                .expectComplete()
                .verify();

    }

    @Test
    public void testGetProductByIdWithIndividualHeader() {
        webClient.get()
                .uri("/lec04/product/{id}", 1)
                .headers(header -> header.set("caller-id", "order-service"))
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(1, p.id()))
                .expectComplete()
                .verify();

    }

    @Test
    public void testErrorResponse() {

        webClient.get()
                .uri("/lec05/calculator/{first}/{second}", 1, 2)
                .headers(header -> header.set("operation", "@"))
                .retrieve()
                .bodyToMono(CalculatorResponseDto.class)
                .doOnError(
                        WebClientResponseException.class,
                        ex -> log.error("{}", ex.getResponseBodyAs(ProblemDetail.class)))
                .onErrorReturn(
                        WebClientResponseException.BadRequest.class,
                        new CalculatorResponseDto(0, 0, "+", 0))
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }


    @Test
    public void testUsingExchangeInsteadOfRetrieve() {

        webClient.get()
                .uri("/lec05/calculator/{first}/{second}", 1, 2)
                .headers(header -> header.set("operation", "+"))
                .exchangeToMono(this::decode)
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    private Mono<CalculatorResponseDto> decode(ClientResponse clientResponse) {
        log.info("Client response headers: {}", clientResponse.headers());
        log.info("Client response status code: {}", clientResponse.statusCode());
        return clientResponse.bodyToMono(CalculatorResponseDto.class);
    }

    @Test
    public void testQueryParameters() {

        var path = "/lec06/calculator";
        var query = "first={first}&second={second}&operation={operation}";
        webClient.get()
                .uri(builder -> builder.path(path).query(query).build(1, 2, "+"))
                .retrieve()
                .bodyToMono(CalculatorResponseDto.class)
                .doOnError(
                        WebClientResponseException.class,
                        ex -> log.error("{}", ex.getResponseBodyAs(ProblemDetail.class)))
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testQueryParametersUsingMap() {

        var path = "/lec06/calculator";
        var query = "first={first}&second={second}&operation={operation}";
        var queryParams = Map.of("first", 1, "second", 2, "operation", "+");
        webClient.get()
                .uri(builder -> builder.path(path).query(query).build(queryParams))
                .retrieve()
                .bodyToMono(CalculatorResponseDto.class)
                .doOnError(
                        WebClientResponseException.class,
                        ex -> log.error("{}", ex.getResponseBodyAs(ProblemDetail.class)))
                .doOnNext(p -> log.info("{}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testBasicAuth() {

        var myWebClient = createWebClient(builder -> builder.defaultHeaders(
                header -> header.setBasicAuth("java", "secret")
        ));

        myWebClient.get()
                .uri("/lec07/product/{id}", 7)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(7, p.id()))
                .expectComplete()
                .verify();

    }

    @Test
    public void testBearerAuth() {

        var myWebClient = createWebClient(builder -> builder.defaultHeaders(
                header -> header.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        ));

        myWebClient.get()
                .uri("/lec08/product/{id}", 7)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(7, p.id()))
                .expectComplete()
                .verify();

    }

    @Test
    public void testExchangeFilter() {

        // attach exchange filter function to client
        var myWebClient = createWebClient(
                builder ->
                        builder.filter(tokenGenerator())
                               .filter(logMethodAndUri())
        );

        myWebClient.get()
                .uri("/lec09/product/{id}", 7)
                .retrieve()
                .bodyToMono(ProductResponseDto.class)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(7, p.id()))
                .expectComplete()
                .verify();

    }

    private ExchangeFilterFunction tokenGenerator() {
        return (request, next) -> {
            var token = UUID.randomUUID().toString().replace("-", "");
            var newRequest = ClientRequest.from(request).headers(h -> h.setBearerAuth(token)).build();
            return next.exchange(newRequest);
        };
    }

    private ExchangeFilterFunction logMethodAndUri() {
        return (request, next) -> {
            var isEnabled = (Boolean) request.attributes().getOrDefault("enable-logging", true);
            if (isEnabled) {
                log.info("Request method: {}", request.method());
                log.info("Request uri: {}", request.url());
            }
            return next.exchange(request);
        };
    }
}
