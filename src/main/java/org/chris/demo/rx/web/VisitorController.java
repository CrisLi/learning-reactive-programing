package org.chris.demo.rx.web;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

@RestController
@RequestMapping("/visitors")
public class VisitorController {

    private UnicastProcessor<UserCheck> hotSource = UnicastProcessor.create();

    private Flux<UserCheck> hotFlux = hotSource.publish().autoConnect();

    @GetMapping("/online")
    public Mono<String> visit(@RequestParam(name = "user", defaultValue = "guest") String user) {
        hotSource.onNext(new UserCheck(user, LocalDateTime.now()));
        return Mono.just("Done");
    }

    // http://localhost:8080/visitors/userStream
    @GetMapping(path = "/userStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserCheck> userStream() {
        return hotFlux;
    }

    @Data
    public static class UserCheck {

        @NonNull
        private String name;

        @NonNull
        private LocalDateTime checkedAt;

    }
}
