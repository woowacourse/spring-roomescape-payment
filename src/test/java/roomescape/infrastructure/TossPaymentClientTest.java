package roomescape.infrastructure;

import org.apache.hc.client5.http.ConnectTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmServerException;

import java.net.SocketTimeoutException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class TossPaymentClientTest {

    public static final String CONNECT_TIMEOUT_TEST_URL = "http://10.255.255.1";
    public static final String READ_TIMEOUT_TEST_URL = "https://httpstat.us/200?sleep=100000";

    @Autowired
    @Qualifier("tossRestClient")
    private RestClient tossRestClient;

    @DisplayName("결제 예외 핸들링 테스트 - INVALID_API_KEY가 발생할 경우")
    @Test
    void paymentExceptionTest() {
        assertThatThrownBy(
                () -> tossRestClient.post()
                        .uri("/v1/payments/confirm")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes()))
                        .header("TossPayments-Test-Code", "INVALID_API_KEY")
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(PaymentConfirmServerException.class);
    }

    @DisplayName("결제 예외 핸들링 테스트 - 유효하지 않은 Secret Key인 걍우")
    @Test
    void paymentExceptionTest2() {
        assertThatThrownBy(
                () -> tossRestClient.post()
                        .uri("/v1/payments/confirm")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("testFail_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes()))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(PaymentConfirmServerException.class);
    }

    @DisplayName("결제 예외 핸들링 테스트 - 유효하지 않은 Secret Key인 걍우")
    @Test
    void paymentExceptionTest3() {
        assertThatThrownBy(
                () -> tossRestClient.post()
                        .uri("/v1/payments/confirm")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " +
                                Base64.getEncoder().encodeToString("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6".getBytes()))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(PaymentConfirmServerException.class);
    }

    @DisplayName("Connect Timeout 테스트")
    @Test
    void connectTimeoutExceptionTest() {
        assertThatThrownBy(
                () -> tossRestClient.post()
                        .uri(CONNECT_TIMEOUT_TEST_URL)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(ResourceAccessException.class)
                .hasCause(new ConnectTimeoutException("Connect to http://10.255.255.1:80 failed: Connect timed out"));
    }

    @DisplayName("Read Timeout 테스트")
    @Test
    void readTimeoutExceptionTest() {
        assertThatThrownBy(
                () -> tossRestClient.post()
                        .uri(READ_TIMEOUT_TEST_URL)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(ResourceAccessException.class)
                .hasCause(new SocketTimeoutException("Read timed out"));
    }
}
