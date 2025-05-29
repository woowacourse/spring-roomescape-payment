package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.controller.PaymentConfirmRequest;

@RestClientTest(PaymentService.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    MockRestServiceServer mockServer;

    @DisplayName("토스에서 FilteredPaymentErrorCode 외의 에러 코드 응답 시, 동일한 내용의 PaymentException을 던진다")
    @Test
    void test1() throws JsonProcessingException {
        //given
        PaymentError paymentError = new PaymentError("NOT_AVAILABLE_BANK", "은행 서비스 시간이 아닙니다.");
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(MockRestResponseCreators.withForbiddenRequest()
                        .body(new ObjectMapper().writeValueAsString(paymentError)));

        //when & then
        PaymentConfirmRequest request = new PaymentConfirmRequest("a", "b", 1);
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentException.class)
                .satisfies(e -> {
                    PaymentException ex = (PaymentException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(FORBIDDEN);
                    assertThat(ex.getMessage()).isEqualTo("은행 서비스 시간이 아닙니다.");
                });
    }

    @DisplayName("토스에서 FilteredPaymentErrorCode 에러 코드 응답 시, INTERNAL_SERVER_ERROR PaymentException을 던진다")
    @Test
    void test2() throws JsonProcessingException {
        //given
        PaymentError paymentError = new PaymentError("INCORRECT_BASIC_AUTH_FORMAT", "aaaa");
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(MockRestResponseCreators.withForbiddenRequest()
                        .body(new ObjectMapper().writeValueAsString(paymentError)));

        //when & then
        PaymentConfirmRequest request = new PaymentConfirmRequest("a", "b", 1);
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentException.class)
                .satisfies(e -> {
                    PaymentException ex = (PaymentException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
                    assertThat(ex.getMessage()).isEqualTo("서버 내부 오류입니다.");
                });
    }
}
