package org.chris.demo.rx.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/github")
public class GithubController {

    private static final String API_URL = "https://api.github.com";

    private WebClient webClient;

    public GithubController() {
        this.webClient = WebClient.create(API_URL);
    }

    // http://localhost:8080/github/users?login=crisli

    @GetMapping("/users")
    public Mono<GithubUser> profile(@RequestParam String login) {
        return webClient
            .get()
            .uri("/users/{login}", login)
            .retrieve()
            .bodyToMono(GithubUser.class);
    }

    // http://localhost:8080/github/repos?login=crisli

    @GetMapping("/repos")
    public Flux<GithubRepo> repos(@RequestParam String login) {
        return webClient
            .get()
            .uri("/users/{login}/repos", login)
            .retrieve()
            .bodyToFlux(GithubRepo.class);
    }

    // http://localhost:8080/github/profile_and_repos?login=crisli

    @GetMapping("/profile_and_repos")
    public Mono<Map<String, Object>> profileAndRepos(@RequestParam String login) {
        return Mono.zip(this.profile(login), this.repos(login).collectList())
            .map(t -> {
                Map<String, Object> json = new HashMap<>(2);
                json.put("user", t.getT1());
                json.put("repos", t.getT2());
                return json;
            });
    }

    @Getter
    @Setter
    public static class GithubUser {

        private String login;
        private int id;
        private String url;
        private String name;

    }

    @Getter
    @Setter
    public static class GithubRepo {

        private int id;
        private String name;
        private String url;

    }
}
