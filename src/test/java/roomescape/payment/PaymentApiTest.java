package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.error.exception.PaymentClientException;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;
import roomescape.reservation.service.PaymentRestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentApiTest {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private PaymentResponseErrorHandler paymentResponseErrorHandler;

    @Autowired
    PaymentRestClient paymentRestClient;

    @Value("${toss.secret-key}")
    private String secretKey;

    @DisplayName("예외 코드를 확인한다.")
    @ParameterizedTest
    @MethodSource("errorMessage")
    void exceptionMessageTest(String errorCode, String errorMessage) {
        // given
        restClientBuilder.defaultHeader("TossPayments-Test-Code", errorCode);
        paymentRestClient = new PaymentRestClient(restClientBuilder, paymentResponseErrorHandler, secretKey);
        Payment payment = new Payment("paymentKey", "orderId", 1000L, "NORMAL");

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approve(payment))
                .isInstanceOf(PaymentClientException.class)
                .hasMessageContaining(errorMessage);
    }

    private static Stream<Arguments> errorMessage() {
        return Stream.of(
                Arguments.arguments("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
                Arguments.arguments("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다."),
                Arguments.arguments("FORBIDDEN_REQUEST", "허용되지 않은 요청입니다."),
                Arguments.arguments("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.")
        );
    }
}
