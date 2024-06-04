package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.exception.PaymentException;
import roomescape.util.LogSaver;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TossPaymentClientTest {

    private final LogSaver logSaver = new LogSaver(new ObjectMapper());
    private final RestClient.Builder testBuilder = new PaymentConfig().createBuilder("toss");
    private MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();

    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        server.reset();
        tossPaymentClient = new TossPaymentClient(testBuilder.build(), logSaver);
    }

    //    @Disabled
    @Test
    void 결제_성공() {
        // given
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());

        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000),
                "paymentType");

        // when && then
        assertThatCode(() -> tossPaymentClient.confirm(paymentRequest)).doesNotThrowAnyException();
    }

    //    @Disabled
    @Test
    void 결제_BAD_REQUEST시_예외_발생() {
        // given
        String errorResponse = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withBadRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000),
                "paymentType");

        // when && then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest)).isInstanceOf(PaymentException.class);
    }

    //    @Disabled
    @Test
    void 결제_SERVER_ERROR시_예외_발생() {
        // given
        String errorResponse = """
                {
                  "code": "FAILED_INTERNAL_SYSTEM_PROCESSING",
                  "message": "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withServerError().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000),
                "paymentType");

        // when && then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest)).isInstanceOf(PaymentException.class);
    }

    //    @Disabled
    @Test
    void 결제_UNAUTHORIZED시_매핑된_SERVER_예외_발생() {
        // given
        String errorResponse = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withUnauthorizedRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000),
                "paymentType");

        // when && then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("clientStatusCode",  HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    //    @Disabled
//    @Test
//    void 타임아웃시_예외_발생() {
//        // given
//
//        String errorResponse = """
//                {
//                  "code": "NOT_FOUND_PAYMENT",
//                  "message": "존재하지 않는 결제 입니다."
//                }
//                """;
//
//        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
//                .andRespond(withException(new ResourceAccessException("요청 시간을 초과하였습니다.")).body(errorResponse).contentType(MediaType.APPLICATION_JSON));
////                .andRespond(withBadRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));
//
//        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000),
//                "paymentType");
//
//        // when && then
//        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest))
//                .isInstanceOf(PaymentException.class)
//                .hasMessage("요청 시간을 초과하였습니다.");
//    }
}

