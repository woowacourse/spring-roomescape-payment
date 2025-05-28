package roomescape.payment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import roomescape.global.error.exception.BadRequestException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(RestClient.RequestBodySpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
        paymentService = new PaymentService(paymentRepository, restClient);
    }

    @Test
    @DisplayName("결제 승인 API를 호출한다.")
    void confirmPayment_Success() {
        // given
        when(restClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(PaymentConfirmRequest.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.onStatus(any()))
                .thenReturn(responseSpec);
        when(responseSpec.body(PaymentConfirmResponse.class))
                .thenReturn(new PaymentConfirmResponse("key", "order", 100L, "CARD"));

        // when
        paymentService.confirmPayment("key", "order", 100L);

        // then
        verify(paymentRepository).save(any());
    }

    @Test
    @DisplayName("결제 승인 API를 호출해서 400 예외가 터진다.")
    void confirmPayment_ThrowsBadRequestException() {
        when(restClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(PaymentConfirmRequest.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.onStatus(any()))
                .thenReturn(responseSpec);
        when(responseSpec.body(PaymentConfirmResponse.class))
                .thenThrow(new BadRequestException("API 오류"));

        assertThatThrownBy(() -> paymentService.confirmPayment("key", "order", 100L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("API 오류");
    }

    @Test
    @DisplayName("결제 승인 API를 호출해서 500 예외가 터진다.")
    void confirmPayment_ThrowsInternalServerError() {
        when(restClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(PaymentConfirmRequest.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.onStatus(any()))
                .thenReturn(responseSpec);
        when(responseSpec.body(PaymentConfirmResponse.class))
                .thenThrow(new BadRequestException("API 오류"));

        assertThatThrownBy(() -> paymentService.confirmPayment("key", "order", 100L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("API 오류");
    }
}
