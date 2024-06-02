package roomescape.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT_REQUEST;
import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT_RESPONSE;

import java.net.URI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import roomescape.dto.PaymentResponse;
import roomescape.service.PaymentService;

@SpringBootTest
class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private PaymentController paymentController;

    @Test
    @DisplayName("결제 생성 정상 동작 시 API 명세대로 응답이 생성되는지 확인")
    void payReservation() {
        // given
        Mockito.when(paymentService.payReservation(DEFAULT_PAYMENT_REQUEST))
                .thenReturn(DEFAULT_PAYMENT_RESPONSE);

        // when
        ResponseEntity<PaymentResponse> response = paymentController.payReservation(DEFAULT_PAYMENT_REQUEST);

        // then
        assertAll(
                () -> Assertions.assertThat(response.getStatusCode())
                        .isEqualTo(HttpStatusCode.valueOf(201)),
                () -> Assertions.assertThat(response.getHeaders().getLocation())
                        .isEqualTo(URI.create("/payments/" + DEFAULT_PAYMENT_RESPONSE.id())),
                () -> Assertions.assertThat(response.getBody())
                        .isEqualTo(DEFAULT_PAYMENT_RESPONSE)
        );
    }
}
