package roomescape.service.booking.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.booking.reservation.module.ReservationRegisterService;

@Sql("/all-test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationRegisterServiceTest {

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
        //given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(7), 1L, 1L, 1L);

        //when
        Reservation reservation = reservationRegisterService.registerReservation(reservationRequest);

        //then
        assertAll(
                () -> assertThat(reservation.getId()).isEqualTo(reservation.getId()),
                () -> assertThat(reservation.getDate()).isEqualTo(reservationRequest.date()),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(reservationRequest.timeId()),
                () -> assertThat(reservation.getMember().getId()).isEqualTo(reservationRequest.memberId()),
                () -> assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED)
        );
    }

    @Test
    void 잘못된_예약_시간대_id로_예약을_등록할_경우_예외_발생() {
        //given
        Long notExistTimeId = timeRepository.findAll().size() + 1L;

        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(), notExistTimeId, 1L, 1L);

        //when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 잘못된_테마_id로_예약을_등록할_경우_예외_발생() {
        //given
        Long notExistIdToFind = themeRepository.findAll().size() + 1L;

        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(), 1L, notExistIdToFind, 1L);

        //when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 날짜와_시간대와_테마가_모두_동일한_예약을_등록할_경우_예외_발생() {
        //given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().minusDays(1), 1L, 1L, 1L);

        //when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 지나간_날짜로_예약을_등록할_경우_예외_발생() {
        //given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().minusDays(1), 1L, 1L, 1L);

        //when, then
        assertThatThrownBy(() -> reservationRegisterService.registerReservation(reservationRequest))
                .isInstanceOf(RoomEscapeException.class);
    }
}
