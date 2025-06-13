package roomescape.payment.service;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import roomescape.global.config.restClient.tosspayment.TossPaymentProperties;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.exception.InvalidPgPaymentException;
import roomescape.payment.tosspayment.TossPgPaymentRestClient;

@TestPropertySource(properties = {
        "payment.pg.toss-payment.secret-key=tosstasstosstasstoss"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PgPaymentServiceInvalidSecretKeyTest {

    private MockWebServer mockWebServer;
    private RestClient testRestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUP() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        TossPaymentProperties properties = new TossPaymentProperties(
                mockWebServer.url("/").toString(),
                "dummySecretKey",
                1000,
                2000
        );

        String basicAuthValue = "Basic " + Base64.getEncoder().encodeToString(
                (properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );

        testRestClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthValue)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("400대 에러가 왔을 때 커스텀 예외 처리 동작")
    class confirmPayment_throwsException_by4xxClientError {

        @DisplayName("secretKey가 유요하지 않을 때 발생하는 예외를 커스텀 예외로 처리한다")
        @Test
        void confirmPayment_throwsException_byInvalidSecretKey() {
            // given
            String mockBodyText = """
                    {
                    	"code": "INVALID_API_KEY",
                    	"message": "잘못된 시크릿키 연동 정보 입니다."
                    }
                    """;

            PgPaymentRequestDto reqDto = new PgPaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
                    .setBody(mockBodyText)
                    .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE));

            // when
            TossPgPaymentRestClient client = new TossPgPaymentRestClient(testRestClient, objectMapper);

            // then
            Assertions.assertThatThrownBy(
                    () -> client.confirmPayment(reqDto)
            ).isInstanceOf(InvalidPgPaymentException.class);
        }
    }
}
