package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.controller.PaymentConfirmRequest;
import roomescape.reservation.controller.PaymentConfirmResponse;

@RestClientTest(PaymentService.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    MockRestServiceServer mockServer;

    @DisplayName("토스에서 결제 승인에 성공하면 예외가 발생하지 않는다.")
    @Test
    void confirm() throws JsonProcessingException {
        //given
        final PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(
                "paymentKey",
                "orderId",
                1000,
                null
        );
        settingMockServerResponse(HttpStatus.OK, paymentConfirmResponse);

        //when & then
        final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
        assertThatCode(() -> paymentService.confirm(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("토스에서 FilteredPaymentErrorCode 외의 에러 코드 응답 시, 동일한 내용의 PaymentException을 던진다")
    @Test
    void confirmException1() throws JsonProcessingException {
        //given
        final PaymentError paymentError = new PaymentError("NOT_AVAILABLE_BANK", "은행 서비스 시간이 아닙니다.");
        settingMockServerResponse(HttpStatus.FORBIDDEN, paymentError);

        //when & then
        final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentException.class)
                .satisfies(e -> {
                    final PaymentException ex = (PaymentException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(FORBIDDEN);
                    assertThat(ex.getMessage()).isEqualTo("은행 서비스 시간이 아닙니다.");
                });
    }

    @DisplayName("토스에서 FilteredPaymentErrorCode 에러 코드 응답 시, INTERNAL_SERVER_ERROR PaymentException을 던진다")
    @Test
    void confirmException2() throws JsonProcessingException {
        //given
        final PaymentError paymentError = new PaymentError("INCORRECT_BASIC_AUTH_FORMAT", "aaaa");
        settingMockServerResponse(HttpStatus.FORBIDDEN, paymentError);

        //when & then
        final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentException.class)
                .satisfies(e -> {
                    final PaymentException ex = (PaymentException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
                    assertThat(ex.getMessage()).isEqualTo("서버 내부 오류입니다.");
                });
    }

    private void settingMockServerResponse(
            final HttpStatus status,
            final Object response
    ) throws JsonProcessingException {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(MockRestResponseCreators.withStatus(status)
                        .body(new ObjectMapper().writeValueAsString(response))
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
