package org.chris.demo.rx.web;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.chris.demo.rx.model.Player;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    @GetMapping("/nationsky")
    public String test() {
        return "Hello Nationsky!";
    }

    @GetMapping("/hello")
    public Mono<String> hello(@RequestParam String name) {
        return Mono.just("Hello " + name);
    }

    @GetMapping("/exchange")
    public Mono<Void> exchange(ServerWebExchange exchange) {

        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        DataBuffer buffer = bufferFactory
            .allocateBuffer()
            .write("exchange".getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @GetMapping("/waiting")
    public Mono<String> waiting() {
        return Mono.never();
    }

    @GetMapping("/error")
    public Mono<String> error() {
        return Mono.error(new RuntimeException("Wow"));
    }

    // http://localhost:8080/demo/players
    @GetMapping("/players")
    public Flux<Player> getPlayers() {
        return Flux.fromIterable(Player.ALL);
    }

    // http -S :8080/demo/players Accept:application/stream+json
    @GetMapping(path = "/players", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Player> getPlayerStream() {
        return Flux.interval(Duration.ofSeconds(2))
            .doOnNext(x -> System.out.println(x))
            .map(i -> Player.ALL.get(i.intValue() % Player.ALL.size()));
    }

}
