package roomescape.config;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled
class PaymentConfigTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentConfigTest.class);
    @LocalServerPort
    int port;

    @Configuration
    static class TimeoutConfig {
        @RestController
        class TimeoutController {
            @GetMapping("/timeout")
            void timeout() {
                try {
                    log.info("start");
                    Thread.sleep(6000);
                    log.info("end");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Autowired
    private ClientHttpRequestFactory timeoutFactory;

    @DisplayName("타임아웃 설정을 확인한다.")
    @Test
    void timeoutTest() {
        RestClient timeoutClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .requestFactory(timeoutFactory)
                .build();

        assertThatCode(() -> timeoutClient.get().uri("/timeout").retrieve().toBodilessEntity())
                .doesNotThrowAnyException();
    }
}
