package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.payment.config.PaymentRestClientConfig;
import roomescape.payment.infrastructure.dto.PaymentFailure;
import roomescape.payment.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.payment.infrastructure.vo.TossPaymentInternalErrorCode;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@RestClientTest
@Import({TossPaymentErrorHandler.class, PaymentRestClientConfig.class})
class TossPaymentClientTest {

    private static final String PAYMENT_KEY = "testKey";
    private static final String ORDER_ID = "testId";
    private static final long AMOUNT = 1_000;

    @Autowired
    private TossPaymentErrorHandler tossPaymentErrorHandler;

    @Autowired
    private RestClient.Builder restClientBuilder;

    private TossPaymentClient tossPaymentClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        tossPaymentClient = new TossPaymentClient(
                restClientBuilder.build(), tossPaymentErrorHandler
        );
    }

    @DisplayName("서버 내부 오류에 해당하는 코드가 반환되는 경우, InternalServerException 예외가 발생한다.")
    @Test
    void throwInternalServerException() throws IOException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(
                new ConfirmPaymentResponse(
                        AMOUNT,
                        PAYMENT_KEY,
                        new PaymentFailure(TossPaymentInternalErrorCode.INVALID_API_KEY.name(), null)
                )
        );

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse)
                );

        ConfirmPaymentRequest request = new ConfirmPaymentRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

        // when & then
        assertThatThrownBy(() -> {
            tossPaymentClient.postConfirmPayment(request, UUID.randomUUID());
        }).isInstanceOf(InternalServerErrorException.class);
     }

    @DisplayName("서버 내부 오류에 해당하지 않는 코드가 반환되는 경우, BadRequestException 예외가 발생한다.")
    @Test
    void throwBadRequestException() throws IOException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(
                new ConfirmPaymentResponse(
                        AMOUNT,
                        PAYMENT_KEY,
                        new PaymentFailure("TEST", null)
                )
        );

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse)
                );

        ConfirmPaymentRequest request = new ConfirmPaymentRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

        // when & then
        assertThatThrownBy(() -> {
            tossPaymentClient.postConfirmPayment(request, UUID.randomUUID());
        }).isInstanceOf(BadRequestException.class);
    }

    @DisplayName("정상 응답을 받는 경우, 예외가 발생하지 않는다.")
    @Test
    void doesNotThrowAnyException() throws IOException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(
                new ConfirmPaymentResponse(
                        AMOUNT,
                        PAYMENT_KEY,
                        null
                )
        );

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse)
                );

        ConfirmPaymentRequest request = new ConfirmPaymentRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

        // when & then
        assertThatCode(() -> {
            tossPaymentClient.postConfirmPayment(request, UUID.randomUUID());
        }).doesNotThrowAnyException();
    }
}
