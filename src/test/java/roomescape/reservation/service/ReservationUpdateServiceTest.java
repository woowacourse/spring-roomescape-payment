package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@ExtendWith(MockitoExtension.class)
class ReservationUpdateServiceTest {
    private static final Reservation RESERVATION1 = new Reservation(
            1L, new Member(1L, "브라운", "brown@abc.com"),
            LocalDate.of(2024, 8, 15),
            new ReservationTime(1L, LocalTime.of(19, 0)),
            new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    private static final MyReservationResponse RESPONSE1 = new MyReservationResponse(
            1L,
            "레벨2 탈출",
            LocalDate.of(2024, 8, 15),
            LocalTime.of(19, 0),
            PaymentStatus.COMPLETED,
            "예약",
            null);

    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationUpdateService reservationUpdateService;

    @DisplayName("id를 통해 예약을 조회할 수 있다.")
    @Test
    void updateReservationPaymentStatusTest() {
        given(reservationRepository.findById(1L)).willReturn(Optional.of(RESERVATION1));
        MyReservationResponse expected = RESPONSE1;

        assertThat(reservationUpdateService.updateReservationPaymentStatus(1L, PaymentStatus.COMPLETED)).isIn(expected);
    }
}
