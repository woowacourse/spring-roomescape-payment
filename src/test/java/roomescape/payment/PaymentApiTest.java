package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.helper.TestFixture.PAYMENT;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import roomescape.reservation.external.client.PaymentRestClient;
import roomescape.reservation.external.error.exception.PaymentClientException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentApiTest {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    PaymentRestClient paymentRestClient;

    @Value("${toss.secret-key}")
    private String secretKey;

    @DisplayName("예외 코드를 확인한다.")
    @ParameterizedTest
    @MethodSource("errorMessage")
    void exceptionMessageTest(String errorCode) {
        // given
        restClientBuilder.defaultHeader("TossPayments-Test-Code", errorCode);
        paymentRestClient = new PaymentRestClient(restClientBuilder.build(), secretKey);

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approve(PAYMENT))
                .isInstanceOf(PaymentClientException.class);
    }

    private static Stream<Arguments> errorMessage() {
        return Stream.of(
                Arguments.arguments("NOT_FOUND_PAYMENT_SESSION"),
                Arguments.arguments("REJECT_CARD_COMPANY"),
                Arguments.arguments("FORBIDDEN_REQUEST"),
                Arguments.arguments("UNAUTHORIZED_KEY")
        );
    }
}
