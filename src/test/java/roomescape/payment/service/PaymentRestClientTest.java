package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import roomescape.common.exception.PaymentException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Role;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.dto.PaymentConfirmResponse;
import roomescape.payment.service.dto.PaymentError;
import roomescape.payment.service.usecase.PaymentQueryUseCase;
import roomescape.payment.service.usecase.PaymentRestClient;

@RestClientTest(PaymentRestClient.class)
class PaymentRestClientTest {

    private static final PaymentVerification VALID_PAYMENT_VERIFICATION = new PaymentVerification(
            "orderId",
            1000,
            Member.withId(
                    1L,
                    MemberName.from("name"),
                    MemberEmail.from("email@email.com"),
                    Role.MEMBER)
    );

    @Autowired
    private PaymentRestClient paymentRestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @MockitoBean
    private PaymentQueryUseCase paymentQueryUseCase;

    @Nested
    class TossPaymentTest {

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
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
            assertThatCode(() -> paymentRestClient.confirm(request, 1L))
                    .doesNotThrowAnyException();
        }

        @DisplayName("토스에서 FilteredPaymentErrorCode 외의 에러 코드 응답 시, 동일한 내용의 PaymentException을 던진다")
        @Test
        void confirmException1() throws JsonProcessingException {
            //given
            final PaymentError paymentError = new PaymentError("NOT_AVAILABLE_BANK", "은행 서비스 시간이 아닙니다.");
            settingMockServerResponse(HttpStatus.FORBIDDEN, paymentError);
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
            assertThatThrownBy(() -> paymentRestClient.confirm(request, 1L))
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
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
            assertThatThrownBy(() -> paymentRestClient.confirm(request, 1L))
                    .isInstanceOf(PaymentException.class)
                    .satisfies(e -> {
                        final PaymentException ex = (PaymentException) e;
                        assertThat(ex.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
                        assertThat(ex.getMessage()).isEqualTo("서버 내부 오류입니다.");
                    });
        }
    }

    @Nested
    class ValidatePaymentConfirmTest {

        @DisplayName("토스에서 클라이언트의 요청 데이터와 PaymentVerification의 데이터가 동일하면 예외가 발생하지 않는다.")
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
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
            assertThatCode(() -> paymentRestClient.confirm(request, 1L))
                    .doesNotThrowAnyException();
        }

        @DisplayName("토스에서 클라이언트의 요청 데이터와 PaymentVerification의 amount 데이터와 다르다면 예외가 발생한다.")
        @Test
        void validatePaymentAmountMismatch() throws JsonProcessingException {
            //given
            final PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(
                    "paymentKey",
                    "orderId",
                    1000,
                    null
            );
            settingMockServerResponse(HttpStatus.OK, paymentConfirmResponse);
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 50);
            assertThatThrownBy(() -> paymentRestClient.confirm(request, 1L))
                    .isInstanceOf(PaymentException.class);
        }

        @DisplayName("토스에서 클라이언트의 요청 데이터와 PaymentVerification의 memberId 데이터와 다르다면 예외가 발생한다.")
        @Test
        void validatePaymentMemberIdMismatch() throws JsonProcessingException {
            //given
            final PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(
                    "paymentKey",
                    "orderId",
                    1000,
                    null
            );
            settingMockServerResponse(HttpStatus.OK, paymentConfirmResponse);
            when(paymentQueryUseCase.getPaymentVerificationByOrderId("orderId"))
                    .thenReturn(VALID_PAYMENT_VERIFICATION);

            //when & then
            final PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 50);
            assertThatThrownBy(() -> paymentRestClient.confirm(request, 1L))
                    .isInstanceOf(PaymentException.class);
        }
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
