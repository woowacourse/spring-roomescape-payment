package roomescape.service.booking.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.service.ServiceBaseTest;
import roomescape.service.booking.reservation.module.ReservationSearchService;

class ReservationSearchServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationSearchService reservationSearchService;

    @Test
    void 단일_예약_조회() {
        // when
        ReservationResponse reservationResponse = reservationSearchService.findReservation(1L);

        // then
        assertAll(
                () -> assertThat(reservationResponse.id()).isEqualTo(1L),
                () -> assertThat(reservationResponse.date()).isEqualTo(LocalDate.now().minusDays(9)),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(3L),
                () -> assertThat(reservationResponse.theme().id()).isEqualTo(12L),
                () -> assertThat(reservationResponse.member().id()).isEqualTo(3L)
        );
    }

    @Test
    void 전체_예약_조회() {
        // when
        List<ReservationResponse> allReservationResponses = reservationSearchService.findAllReservations();

        // then
        assertThat(allReservationResponses).hasSize(32);
    }

    @Test
    void 특정_사용자로_필터링_후_예약_조회() {
        // given
        Long filteringUserId = USER_ID;
        ReservationFilter reservationFilter = new ReservationFilter();
        reservationFilter.setMemberId(filteringUserId);

        // when
        List<ReservationResponse> reservationResponses = reservationSearchService.findReservationsByFilter(
                reservationFilter);

        // then
        assertThat(reservationResponses).isNotEmpty()
                .allMatch(r -> r.member().id().equals(filteringUserId));
    }

    @Test
    void 특정_테마와_날짜로_필터링_후_예약_조회() {
        // given
        Long filteringThemeId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().minusDays(1);

        ReservationFilter reservationFilter = new ReservationFilter();
        reservationFilter.setThemeId(filteringThemeId);
        reservationFilter.setStartDate(startDate);
        reservationFilter.setEndDate(endDate);

        // when
        List<ReservationResponse> reservationResponses = reservationSearchService.findReservationsByFilter(
                reservationFilter);

        // then
        assertThat(reservationResponses).isNotEmpty()
                .allMatch(r ->
                        r.theme().id().equals(filteringThemeId) &&
                                (r.date().isEqual(startDate) || r.date().isAfter(startDate)) &&
                                (r.date().isEqual(endDate) || r.date().isBefore(endDate))
                );
    }

    @Test
    void 존재하지_않는_id로_조회할_경우_예외_발생() {
        // given
        Long notExistIdToFind = reservationSearchService.findAllReservations().size() + 1L;

        // when, then
        assertThatThrownBy(() -> reservationSearchService.findReservation(notExistIdToFind))
                .isInstanceOf(RoomEscapeException.class);
    }
}
