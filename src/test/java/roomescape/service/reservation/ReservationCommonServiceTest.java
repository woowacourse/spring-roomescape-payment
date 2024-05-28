package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

class ReservationCommonServiceTest extends ReservationServiceTest {

    @Autowired
    private ReservationCommonService reservationCommonService;
    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("모든 예약 내역을 조회한다.")
    @Test
    @Sql({"/truncate-with-time-and-theme.sql", "/insert-past-reservation.sql"})
    void findAllReservations() {
        //when
        List<ReservationResponse> reservations = reservationCommonService.findAll();

        //then
        assertThat(reservations).hasSize(3);
    }

    @DisplayName("사용자 조건으로 예약 내역을 조회한다.")
    @Test
    void findByMember() {
        //given
        Reservation reservation = new Reservation(member, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
        reservationRepository.save(reservation);
        ReservationFilterRequest reservationFilterRequest = new ReservationFilterRequest(member.getId(), null, null,
            null);

        //when
        List<ReservationResponse> reservations = reservationCommonService.findByCondition(reservationFilterRequest);

        //then
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("사용자와 테마 조건으로 예약 내역을 조회한다.")
    @Test
    void findByMemberAndTheme() {
        //given
        Reservation reservation = new Reservation(member, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
        reservationRepository.save(reservation);
        long notMemberThemeId = theme.getId() + 1;
        ReservationFilterRequest reservationFilterRequest = new ReservationFilterRequest(member.getId(),
            notMemberThemeId, null, null);

        //when
        List<ReservationResponse> reservations = reservationCommonService.findByCondition(reservationFilterRequest);

        //then
        assertThat(reservations).isEmpty();
    }

    @DisplayName("관리자가 id로 예약을 삭제한다.")
    @Test
    void deleteReservationById() {
        //given
        Reservation reservation = new Reservation(admin, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
        Reservation target = reservationRepository.save(reservation);

        //when
        reservationRepository.deleteById(target.getId());

        //then
        assertThat(reservationCommonService.findAll()).isEmpty();
    }

    @DisplayName("관리자가 예약을 삭제하고, 예약 대기가 있다면 가장 우선순위가 높은 예약 대기를 예약으로 전환한다.")
    @Test
    void deleteThenUpdateReservation() {
        //given
        Reservation reservation = new Reservation(admin, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
        Reservation reservation2 = new Reservation(member, reservationDetail, ReservationStatus.WAITING, Payment.createEmpty());
        Reservation reservation3 = new Reservation(anotherMember, reservationDetail, ReservationStatus.WAITING, Payment.createEmpty());
        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        //when
        reservationCommonService.deleteById(reservation.getId());

        //then
        List<ReservationWithRank> reservations = reservationRepository.findWithRankingByMemberId(member.getId());
        assertThat(reservations.get(0).getReservation().isReserved()).isTrue();
    }

    @DisplayName("관리자가 과거 예약을 삭제하려고 하면 예외가 발생한다.")
    @Test
    @Sql({"/truncate.sql", "/insert-past-reservation.sql"})
    void cannotDeleteReservationByIdIfPast() {
        //given
        long id = 1;

        //when & then
        assertThatThrownBy(() -> reservationCommonService.deleteById(id))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessage("이미 지난 예약은 삭제할 수 없습니다.");
    }
}
