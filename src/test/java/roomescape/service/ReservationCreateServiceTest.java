package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.BasicAcceptanceTest;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.exception.RoomescapeException;

class ReservationCreateServiceTest extends BasicAcceptanceTest {
    @Autowired
    private ReservationCreateService reservationCreateService;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("존재하지 않는 예약 시간으로 예약을 생성시 예외를 반환한다.")
    @Test
    void shouldReturnIllegalArgumentExceptionWhenNotFoundReservationTime() {
        AdminReservationRequest adminReservationRequest = new AdminReservationRequest(
                1L, LocalDate.now().plusDays(1), 999L, 1L
        );

        assertThatThrownBy(() -> reservationCreateService.saveReservationByAdmin(adminReservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("존재하지 않는 예약 시간입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 테마로 예약을 생성시 예외를 반환한다.")
    void shouldThrowIllegalArgumentExceptionWhenNotFoundTheme() {
        AdminReservationRequest adminReservationRequest = new AdminReservationRequest(
                1L, LocalDate.now().plusDays(1), 1L, 999L
        );

        assertThatThrownBy(() -> reservationCreateService.saveReservationByAdmin(adminReservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("존재하지 않는 테마입니다.");
    }

    @DisplayName("자신이 한 예약과 동일한 예약을 하는 경우 예외를 반환한다.")
    @Test
    void shouldReturnIllegalStateExceptionWhenDuplicatedReservationCreate() {
        Reservation existReservation = reservationRepository.findAll().get(0);
        AdminReservationRequest adminReservationRequest = new AdminReservationRequest(
                existReservation.getMember().getId(),
                existReservation.getDate(),
                existReservation.getTime().getId(),
                existReservation.getTheme().getId()
        );

        assertThatThrownBy(() -> reservationCreateService.saveReservationByAdmin(adminReservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("예약이 존재합니다.");
    }

    @DisplayName("과거 시간을 예약하는 경우 예외를 반환한다.")
    @Test
    void shouldThrowsIllegalArgumentExceptionWhenReservationDateIsBeforeCurrentDate() {
        AdminReservationRequest adminReservationRequest = new AdminReservationRequest(
                1L, LocalDate.now().minusDays(1), 1L, 1L
        );

        assertThatThrownBy(() -> reservationCreateService.saveReservationByAdmin(adminReservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("현재 시간보다 과거로 예약할 수 없습니다.");
    }
}
