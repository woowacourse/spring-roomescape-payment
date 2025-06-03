package roomescape.global.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@SpringBootTest
@TestPropertySource(properties = {
        "toss.payment.secret-key=test_sk_secret"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestClientConfigTest {

    private MockWebServer mockWebServer;
    private RestClient restClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClientConfig config = new RestClientConfig(
                "test_sk_secret",
                String.format("http://localhost:%s", mockWebServer.getPort()),
                1000,
                2000);
        restClient = config.restClient();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("정상적인 응답시간 내에는 응답을 받을 수 있다")
    void successWithinTimeout() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{test body}")
                .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE));

        // when & then
        String response = restClient.get()
                .uri("/test")
                .retrieve()
                .body(String.class);

        assertThat(response).contains("test body");
    }

    @Test
    @DisplayName("Read Timeout(2초) 초과시 예외가 발생한다")
    void throwsExceptionWhenReadTimeout() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{test body}")
                .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBodyDelay(3, TimeUnit.SECONDS));

        // when & then
        assertThatThrownBy(() ->
                restClient.get()
                        .uri("/test")
                        .retrieve()
                        .body(String.class)
        ).isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("Connect Timeout(1초) 초과시 예외가 발생한다")
    void throwsExceptionWhenConnectTimeout() throws IOException {
        // given
        mockWebServer.shutdown(); // 서버를 종료하여 연결 불가능한 상태로 만듦

        // when & then
        assertThatThrownBy(() ->
                restClient.get()
                        .uri("/test")
                        .retrieve()
                        .body(String.class)
        ).isInstanceOf(ResourceAccessException.class)
                .hasMessageContaining("Connection refused");
    }
} 
