package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentApproveSuccessDto;

@RestClientTest({TossPaymentClient.class, PaymentAuthorizationGenerator.class})
class TossPaymentClientTest {

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("결제 승인을 요청하고 올바르게 응답을 반환한다.")
    @Test
    void approve() throws IOException {
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "orderId", 1000L);
        String paymentApproveJson = objectMapper.writeValueAsString(paymentApproveDto);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(content().json(paymentApproveJson))
                .andRespond(withSuccess(paymentApproveJson, MediaType.APPLICATION_JSON));

        PaymentApproveSuccessDto paymentApproveSuccessDto = tossPaymentClient.approve(paymentApproveDto);

        assertAll(
                () -> assertThat(paymentApproveSuccessDto.paymentKey()).isEqualTo(paymentApproveDto.paymentKey()),
                () -> assertThat(paymentApproveSuccessDto.orderId()).isEqualTo(paymentApproveDto.orderId()),
                () -> assertThat(paymentApproveSuccessDto.totalAmount()).isEqualTo(paymentApproveDto.totalAmount())
        );

        server.verify();
    }

    @DisplayName("결제 승인 실패 시 예외가 발생한다.")
    @Test
    void invalidPaymentApproveRequest() throws IOException {
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("invalidKey", "orderId", 1000L);
        String paymentApproveJson = objectMapper.writeValueAsString(paymentApproveDto);
        String responseJson = objectMapper.writeValueAsString(
                new RoomescapeException(RoomescapeErrorCode.PAYMENT_FAILED,
                        String.format("결제 승인 요청 처리 중 예외가 발생했습니다.")));

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(content().json(paymentApproveJson))
                .andRespond(withBadRequest().body(responseJson));

        assertThatCode(() -> tossPaymentClient.approve(paymentApproveDto))
                .isInstanceOf(RoomescapeException.class);

        server.verify();
    }

    @DisplayName("토스 서버에서 발생한 문제로 예외가 발생한다.")
    @Test
    void tossInternalServerErrorTest() throws IOException {
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "orderId", 1000L);
        String paymentApproveJson = objectMapper.writeValueAsString(paymentApproveDto);
        String responseJson = objectMapper.writeValueAsString(
                new RoomescapeException(RoomescapeErrorCode.INTERNAL_SERVER_ERROR,
                        String.format("결제 승인 처리 API 서버에서 에러가 발생했습니다.")));

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(content().json(paymentApproveJson))
                .andRespond(withServerError().body(responseJson));

        assertThatCode(() -> tossPaymentClient.approve(paymentApproveDto))
                .isInstanceOf(RoomescapeException.class);

        server.verify();
    }
}
