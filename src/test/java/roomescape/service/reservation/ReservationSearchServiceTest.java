package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.Status;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.service.ServiceBaseTest;

class ReservationSearchServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationSearchService reservationSearchService;

    @Autowired
    ReservationRepository reservationRepository;

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
        List<ReservationResponse> allReservationResponses = reservationSearchService.findAllReservedReservations();

        // then
        assertThat(allReservationResponses).hasSize(32);
    }

    @Test
    void 특정_사용자로_필터링_후_예약_조회() {
        // given
        Long filteringUserId = USER_ID;
        ReservationFilter reservationFilter = new ReservationFilter(filteringUserId, null, null, null);

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

        ReservationFilter reservationFilter = new ReservationFilter(null, filteringThemeId, startDate, endDate);

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
    void 특정_사용자의_모든_예약_조회() {
        // when
        List<UserReservationResponse> responses = reservationSearchService.findReservationByMemberId(3L);

        // then
        List<Long> result = responses.stream()
                .map(response -> reservationRepository.findByIdOrThrow(response.id()).getMember().getId())
                .toList();
        assertThat(result).isNotEmpty().allMatch(r -> r == 3L);
    }

    @Test
    void 특정_사용자의_모든_예약_조회시_예약_대기의_경우_대기_순서를_표시() {
        // when
        List<UserReservationResponse> responses = reservationSearchService.findReservationByMemberId(4L);

        // then
        UserReservationResponse result = responses.stream()
                .filter(response -> response.id() == 32L)
                .findFirst()
                .orElseThrow();

        assertThat(result.waitingOrder()).isEqualTo(2);
    }

    @Test
    void 모든_예약_대기_상태의_예약을_조회() {
        // when
        List<ReservationResponse> responses = reservationSearchService.findAllWaitingReservations();

        // then
        List<Status> result = responses.stream()
                .map(response -> reservationRepository.findByIdOrThrow(response.id()).getStatus())
                .toList();

        assertThat(result).isNotEmpty().allMatch(r -> r == Status.WAITING);
    }
}
