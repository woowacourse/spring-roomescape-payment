package roomescape.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import roomescape.common.exception.impl.TossConfirmException;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.application.dto.TossErrorResponse;

@RestClientTest(TossPaymentGatewayClient.class)
class TossPaymentGatewayClientTest {

    @Autowired
    private TossPaymentGatewayClient tossPaymentGatewayClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 결제_승인_요청을_처리한다() throws Exception {
        TossConfirmRequest request = new TossConfirmRequest("key", "orderId", 1000L);
        TossConfirmResponse expected = new TossConfirmResponse("key", "orderId",
            new TossConfirmResponse.EasyPayInfo(1000L));

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andRespond(withSuccess(
                objectMapper.writeValueAsString(expected),
                MediaType.APPLICATION_JSON
            ));

        TossConfirmResponse response = tossPaymentGatewayClient.processPaymentConfirm(request);

        assertAll(
            () -> assertThat(response.paymentKey()).isEqualTo("key"),
            () -> assertThat(response.orderId()).isEqualTo("orderId"),
            () -> assertThat(response.easyPay().amount()).isEqualTo(1000L)
        );
    }

    @Test
    void 클라이언트_에러가_발생하면_예외를_던진다() throws Exception {
        TossConfirmRequest request = new TossConfirmRequest("key", "orderId", 1000L);
        TossErrorResponse errorResponse = new TossErrorResponse("INVALID_REQUEST", "요청이 잘못되었습니다");

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andRespond(withJsonError(HttpStatus.BAD_REQUEST, errorResponse));

        assertThatThrownBy(() -> tossPaymentGatewayClient.processPaymentConfirm(request))
            .isInstanceOf(TossConfirmException.class)
            .satisfies(e -> {
                TossConfirmException ex = (TossConfirmException) e;
                assertAll(
                    () -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(ex.getCode()).isEqualTo("INVALID_REQUEST"),
                    () -> assertThat(ex.getMessage()).isEqualTo("요청이 잘못되었습니다")
                );
            });
    }

    @Test
    void 서버_에러가_발생하면_예외를_던진다() throws Exception {
        TossConfirmRequest request = new TossConfirmRequest("key", "orderId", 1000L);
        TossErrorResponse errorResponse = new TossErrorResponse("SERVER_ERROR", "서버 오류 발생");

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andRespond(withJsonError(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse));

        assertThatThrownBy(() -> tossPaymentGatewayClient.processPaymentConfirm(request))
            .isInstanceOf(TossConfirmException.class)
            .satisfies(e -> {
                TossConfirmException ex = (TossConfirmException) e;
                assertAll(
                    () -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                    () -> assertThat(ex.getCode()).isEqualTo("SERVER_ERROR"),
                    () -> assertThat(ex.getMessage()).isEqualTo("서버 오류 발생")
                );
            });
    }

    private DefaultResponseCreator withJsonError(final HttpStatus status,
        final TossErrorResponse error) throws Exception {
        return withStatus(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(error), StandardCharsets.UTF_8);
    }
}
