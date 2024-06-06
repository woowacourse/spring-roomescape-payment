package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_NOT_FOUND;
import static roomescape.fixture.TestFixture.RESERVATION_ONE;
import static roomescape.fixture.TestFixture.paymentConfirmRequest;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.component.TossPaymentClient;
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
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(RESERVATION_ONE()));

        assertDoesNotThrow(() -> paymentService.confirm(paymentConfirmRequest(), 1L));
    }

    @Test
    @DisplayName("존재하지 않는 예약에 대한 결제 승인 요청에 실패한다.")
    void confirmFailure() {
        assertThatThrownBy(() -> paymentService.confirm(paymentConfirmRequest(), 3L))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(RESERVATION_NOT_FOUND.message());
    }
}
