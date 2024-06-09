package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ReservationRegisterServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationRegisterService reservationRegisterService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Test
    void 예약_등록() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(7), 1L, 1L, USER_ID);

        // when
        ReservationResponse reservationResponse = reservationRegisterService.registerReservation(reservationRequest);

        // then
        Reservation reservation = reservationRepository.findById(reservationResponse.id()).orElseThrow();
        assertAll(
                () -> assertThat(reservation.getDate()).isEqualTo(reservationRequest.date()),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(reservationRequest.timeId()),
                () -> assertThat(reservation.getMember().getId()).isEqualTo(reservationRequest.memberId()),
                () -> assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED)
        );
    }

    @Test
    void 잘못된_예약_시간대_id로_예약을_등록할_경우_예외_발생() {
        // given
        Long notExistTimeId = timeRepository.findAll().size() + 1L;

        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(), notExistTimeId, 1L, USER_ID);

        // when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 잘못된_테마_id로_예약을_등록할_경우_예외_발생() {
        // given
        Long notExistIdToFind = themeRepository.findAll().size() + 1L;

        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(), 1L, notExistIdToFind, USER_ID);

        // when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 날짜와_시간대와_테마가_모두_동일한_예약을_등록할_경우_예외_발생() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(1), 1L, 1L, USER_ID);

        // when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 지나간_날짜로_예약을_등록할_경우_예외_발생() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().minusDays(1), 1L, 1L, USER_ID);

        // when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }
}
