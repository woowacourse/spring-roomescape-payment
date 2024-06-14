package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

class ReservationQueryServiceTest extends ReservationServiceTest {

    @Autowired
    private ReservationCommandService reservationCommandService;
    @Autowired
    private ReservationQueryService reservationQueryService;
    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("모든 예약 내역을 조회한다.")
    @Test
    void findAllReservations() {
        Reservation reservation = new Reservation(admin, reservationDetail, ReservationStatus.RESERVED);
        reservationRepository.save(reservation);
        //when
        List<ReservationResponse> reservations = reservationQueryService.findAll();

        //then
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("사용자 조건으로 예약 내역을 조회한다.")
    @Test
    void findByMember() {
        //given
        Reservation reservation = new Reservation(member, reservationDetail, ReservationStatus.RESERVED);
        reservationRepository.save(reservation);
        ReservationFilterRequest reservationFilterRequest = new ReservationFilterRequest(member.getId(), null, null,
            null);

        //when
        List<ReservationResponse> reservations = reservationQueryService.findByCondition(reservationFilterRequest);

        //then
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("사용자와 테마 조건으로 예약 내역을 조회한다.")
    @Test
    void findByMemberAndTheme() {
        //given
        Reservation reservation = new Reservation(member, reservationDetail, ReservationStatus.RESERVED);
        reservationRepository.save(reservation);
        long notMemberThemeId = theme.getId() + 1;
        ReservationFilterRequest reservationFilterRequest = new ReservationFilterRequest(member.getId(),
            notMemberThemeId, null, null);

        //when
        List<ReservationResponse> reservations = reservationQueryService.findByCondition(reservationFilterRequest);

        //then
        assertThat(reservations).isEmpty();
    }

    @DisplayName("관리자가 id로 예약을 삭제한다.")
    @Test
    void deleteReservationById() {
        //given
        Reservation reservation = new Reservation(admin, reservationDetail, ReservationStatus.RESERVED);
        Reservation target = reservationRepository.save(reservation);

        //when
        reservationRepository.deleteById(target.getId());

        //then
        assertThat(reservationQueryService.findAll()).isEmpty();
    }

    @DisplayName("관리자가 과거 예약을 삭제하려고 하면 예외가 발생한다.")
    @Test
    @Sql({"/truncate.sql", "/time.sql", "/theme.sql", "/reservation-detail.sql", "/member.sql", "/reservation.sql"})
    void cannotDeleteReservationByIdIfPast() {
        //given
        long id = 1;

        //when & then
        assertThatThrownBy(() -> reservationCommandService.deleteById(id))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessage("이미 지난 예약은 삭제할 수 없습니다.");
    }
}
