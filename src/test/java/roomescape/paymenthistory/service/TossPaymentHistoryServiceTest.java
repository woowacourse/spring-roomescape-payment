package roomescape.paymenthistory.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.paymenthistory.domain.PaymentHistory;
import roomescape.paymenthistory.domain.TossPaymentRestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.repository.PaymentHistoryRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@ExtendWith(MockitoExtension.class)
class TossPaymentHistoryServiceTest {

    private static final Reservation RESERVATION = new Reservation(1L,
            MemberFixture.MEMBER_BRI, LocalDate.now().plusDays(1), TimeFixture.TIME_1, ThemeFixture.THEME_1,
            ReservationStatus.RESERVED);

    @InjectMocks
    private TossPaymentHistoryService tossPaymentHistoryService;

    @Mock
    private TossPaymentRestClient tossPaymentRestClient;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest("paymentKey",
                "orderId", 128000, RESERVATION);

        doNothing().when(tossPaymentRestClient).approvePayment(paymentCreateRequest);

        assertThatCode(() -> tossPaymentHistoryService.approvePayment(paymentCreateRequest))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        doNothing().when(tossPaymentRestClient).cancelPayment("paymentKey");
        when(paymentHistoryRepository.findByReservation_Id(1L))
                .thenReturn(Optional.of(new PaymentHistory(RESERVATION, "paymentKey", 128000)));

        assertThatCode(() -> tossPaymentHistoryService.cancelPayment(1L))
                .doesNotThrowAnyException();
    }

    @DisplayName("일치하는 결제 기록이 없는 경우 에러를 던진다.")
    @Test
    void cancelPayment_whenDoseNotExistHistory() {
        Reservation reservation = ReservationFixture.RESERVATION_WITH_ID;

        when(paymentHistoryRepository.findByReservation_Id(reservation.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tossPaymentHistoryService.cancelPayment(reservation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 기록이 존재하지 않습니다.");
    }
}
