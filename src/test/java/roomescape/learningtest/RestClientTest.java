package roomescape.learningtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class RestClientTest {

    @Test
    @DisplayName("get method 요청- String")
    void get_method_request_as_string() {
        // given
        RestClient client = RestClient.builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .build();

        // when & then
        assertThatCode(() -> client.get()
                .uri("/posts")
                .retrieve()
                .body(String.class)
        )
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("get method 요청- Json")
    void get_method_request_as_Json() {
        // given
        RestClient client = RestClient.builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .build();

        List<Post> posts = client.get()
                .uri("/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Post>>() {
                });

        // when & then
        assertThat(posts).hasSize(100);
    }

    @Test
    @DisplayName("post method 요청")
    void post_method_request() {
        // given
        RestClient client = RestClient.builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .build();

        Post post = new Post(1L, 2L, "title", "body");

        // when
        Post responseBody = client.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(post)
                .retrieve()
                .body(Post.class);

        // then
        assertThat(responseBody)
                .isNotNull();
    }

    @Test
    @DisplayName("에러 핸들링")
    void error_handle() {
        // given
        RestClient client = RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory())
                .baseUrl("http://jsonplaceholder.typicode.com")
                .build();

        // when & then
        assertThatCode(() ->
                client.get()
                        .uri("helloiamfinethankyouandyou")
                        .exchange((request, response) -> response.getStatusCode().is4xxClientError() ? "hello" : "")
        ).doesNotThrowAnyException();
    }
}
