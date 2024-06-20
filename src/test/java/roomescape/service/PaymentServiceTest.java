package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static roomescape.exception.RoomescapeErrorCode.RESERVATION_NOT_FOUND;
import static roomescape.fixture.ReservationFixture.reservationFixture;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.component.TossPaymentClient;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TossPaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentService paymentService;


    @Test
    @DisplayName("결제 승인 요청에 성공한다. ")
    void confirmSuccess() {
        var request = new PaymentConfirmRequest("expectedKey", "expectedId", 1000L, 1L);
        var response = new PaymentConfirmResponse("expectedKey", "expectedId", 1000L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservationFixture(1)));
        when(paymentClient.confirm(any())).thenReturn(response);

        assertDoesNotThrow(() -> paymentService.confirm(request));
    }

    @Test
    @DisplayName("존재하지 않는 예약에 대한 결제 승인 요청을 할 경우 예외가 발생한다.")
    void confirmReservationNotExistsFailure() {
        var request = new PaymentConfirmRequest("expectedKey", "expectedId", 1000L, 1L);

        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(RESERVATION_NOT_FOUND.message());
    }
}
