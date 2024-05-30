package roomescape.learningtest.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentConfigTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentConfigTest.class);
    @LocalServerPort
    int port;

    @TestConfiguration
    static class TimeoutConfig {
        @RestController
        class TimeoutController {
            @GetMapping("/timeout")
            void timeout() {
                log.info("start");
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                }
                log.info("end");
            }
        }
    }

    @DisplayName("학습 테스트 - 타임아웃 설정 방법을 학습한다.")
    @Test
    void timeoutTest() {
        SimpleClientHttpRequestFactory timeoutFactory = new SimpleClientHttpRequestFactory();
        timeoutFactory.setConnectTimeout(Duration.ofMillis(1));
        timeoutFactory.setReadTimeout(Duration.ofMillis(1));

        RestClient timeoutClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .requestFactory(timeoutFactory)
                .build();

        assertThatThrownBy(() -> timeoutClient.get().uri("/timeout").retrieve().toBodilessEntity())
                .isInstanceOf(RestClientException.class);
    }
}
