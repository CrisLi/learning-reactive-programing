package org.chris.demo.rx.ps;

import static org.chris.demo.rx.model.Player.MBAPPÉ;
import static org.chris.demo.rx.model.Player.MESSI;
import static org.chris.demo.rx.model.Player.MODRIC;
import static org.chris.demo.rx.model.Player.NEYMAR;
import static org.chris.demo.rx.model.Player.RONALDO;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.chris.demo.rx.model.Player;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxDemo {

    public static void main(String[] args) {

        mergeWith()
            .subscribe(
                data -> log.info(data.toString()), // Data Channel
                error -> log.error("Error!", error), // Error Channel
                () -> log.info("Done!") // Complete Channel
            );

        sleep(10);
    }

    public static Flux<Player> fromIterable() {
        return Flux.fromIterable(Arrays.asList(RONALDO, MESSI, MBAPPÉ, NEYMAR));
    }

    public static Flux<Player> just() {
        return Flux.just(MODRIC, MBAPPÉ);
    }

    public static Flux<String> map() {
        return fromIterable().map(p -> p.getName());
    }

    public static Flux<Player> flatMap() {
        return Flux.range(0, 2)
            .flatMap(i -> {
                if (i > 0) {
                    return fromIterable();
                }
                return just();
            });
    }

    public static Flux<Player> filter() {
        return fromIterable().filter(p -> p.getAge() > 30);
    }

    public static Flux<Player> error() {
        return fromIterable()
            .doOnNext(p -> {
                if (p.getAge() < 30) {
                    throw new IllegalStateException("this is is an error");
                }
            });
    }

    public static Flux<Player> checkedException() {
        return fromIterable()
            .doOnNext(p -> {
                // some operation will throw checked exception
                if (p.getName().equals(NEYMAR.getName())) {
                    throw Exceptions.propagate(new IOException("this is a checked exception"));
                }
            })
            .onErrorResume(IOException.class, (e) -> Flux.just(MODRIC));
    }

    public static Flux<Player> retry() {
        return fromIterable()
            .doOnNext(p -> {
                if (p.getAge() < 30) {
                    throw new IllegalStateException("this is is an error");
                }
            })
            .retry(3);
    }

    public static Flux<Player> onErrorResume() {
        return error().onErrorResume(IllegalStateException.class, (e) -> Flux.just(MODRIC));
    }

    public static Flux<Long> interval(long seconds) {
        return Flux.interval(Duration.ofSeconds(seconds));
    }

    public static Flux<String> zip1() {
        return Flux.zip(interval(1), fromIterable())
            .map(t -> t.getT1() + " " + t.getT2().getName());
    }

    public static Flux<String> zip2() {
        return Flux.zip(interval(2), just())
            .map(t -> t.getT1() + " " + t.getT2().getName());
    }

    public static Flux<String> mergeWith() {
        return zip1().mergeWith(zip2());
    }

    public static void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
