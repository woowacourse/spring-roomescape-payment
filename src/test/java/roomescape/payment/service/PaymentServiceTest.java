package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import roomescape.payment.PaymentErrorResponse;
import roomescape.payment.PaymentRequest;
import roomescape.payment.PaymentResponse;
import roomescape.payment.exception.PaymentException;
import roomescape.util.ServiceTest;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest extends ServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private PaymentClient paymentClient;

    @DisplayName("결제에 성공하면, 응답을 반환한다.")
    @Test
    void successfulPayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(1000L, "MC45NTg4ODYxMzA5MTAz", "tgen_20240528172021mxEG4");
        ResponseEntity<PaymentResponse> okResponse = ResponseEntity.ok(
                new PaymentResponse("tgen_20240528163518tcin", "DONE", "MC4wOTA5NzEwMjg3MjQ2", 1000L));
        doReturn(okResponse).when(paymentClient).confirm(paymentRequest,"encode-secret-key");

        //when
        final PaymentResponse response = paymentService.confirm(paymentRequest);

        //then
        assertThat(response.paymentKey()).isEqualTo("tgen_20240528163518tcin");
    }

    @DisplayName("결제 중 예외가 발생한다.")
    @Test
    void throw_exception() {
        //given
        doThrow(new PaymentException(new PaymentErrorResponse("NOT_FOUND_PAYMENT", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.")))
                .when(paymentClient).confirm(any(),anyString());
        PaymentRequest paymentRequest = new PaymentRequest(1000L, "MC45NTg4ODYxMzA5MTAz", "tgen_20240528172021mxEG4");

        //when&then
        assertThatThrownBy(() -> paymentService.confirm(paymentRequest))
                .isInstanceOf(PaymentException.class);
    }
}
