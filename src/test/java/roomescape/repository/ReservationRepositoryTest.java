package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.exception.RoomEscapeException;

@Sql("/test-data.sql")
class ReservationRepositoryTest extends RepositoryBaseTest {

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void 주어진_id에_해당하는_예약_조회() {
        // when
        Reservation reservation = reservationRepository.findById(USER_ID).orElseThrow();

        // then
        assertThat(reservation.getId()).isEqualTo(USER_ID);
    }

    @Test
    void 존재하지_않는_id로_조회시_예외_발생() {
        // when, then
        assertThatThrownBy(() -> reservationRepository.findByIdOrThrow(1000L))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 주어진_멤버로_예약된_모든_예약_조회() {
        // when
        List<Reservation> reservations = reservationRepository.findByMemberId(USER_ID);

        // then
        assertThat(reservations).isNotEmpty()
                .allMatch(r -> r.getMember().getId().equals(USER_ID));
    }

    @Test
    void 주어진_멤버_또는_테마_또는_기간_또는_상태에_해당하는_모든_예약_조회시_모든_항목_제공() {
        // given
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // when
        List<Reservation> findByAllFilter = reservationRepository.findByMemberOrThemeOrDateRangeAndStatus(
                USER_ID, 1L, startDate, endDate, Status.RESERVED);

        // then
        assertThat(findByAllFilter).isNotEmpty()
                .allMatch(r -> r.getMember().getId().equals(USER_ID) &&
                        r.getTheme().getId().equals(1L) &&
                        (!r.getDate().isBefore(startDate) && !r.getDate().isAfter(endDate)) &&
                        r.getStatus() == Status.RESERVED
                );
    }

    @Test
    void 주어진_멤버_또는_테마_또는_기간에_해당하는_모든_예약_조회시_멤버와_테마_제공() {
        // when
        List<Reservation> reservations = reservationRepository.findByMemberOrThemeOrDateRangeAndStatus(
                USER_ID, 1L, null, null, Status.RESERVED);

        // then
        assertThat(reservations).isNotEmpty()
                .allMatch(r -> r.getMember().getId().equals(USER_ID) &&
                        r.getTheme().getId().equals(1L) &&
                        r.getStatus() == Status.RESERVED
                );
    }

    @Test
    void 주어진_상태와_동일한_예약을_모두_조회() {
        // when
        List<Reservation> reservations = reservationRepository.findByStatusEquals(Status.WAITING);

        // then
        assertThat(reservations).isNotEmpty().allMatch(r -> r.getStatus() == Status.WAITING);
    }

    @Test
    void 주어진_기간_동안_가장_예약이_많은_테마를_10개까지_조회() {
        // given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // when
        List<Long> topThemeIds = reservationRepository.findTopThemeIdsByReservationCountsForDate(
                startDate, endDate);

        // then
        assertAll(
                () -> assertThat(topThemeIds).hasSize(10),
                () -> assertThat(topThemeIds.get(0)).isEqualTo(1L),
                () -> assertThat(topThemeIds.get(1)).isEqualTo(2L),
                () -> assertThat(topThemeIds).doesNotContain(12L)
        );
    }

    @Test
    void 주어진_예약과_동일한_조건_및_상태의_예약_중에_몇번쨰_순서인지_확인() {
        // given
        Reservation reservation = reservationRepository.findById(32L).orElseThrow();

        // when
        int result = reservationRepository.findReservationOrder(reservation);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 주어진_시간에_예약이_있는지_확인() {
        // when
        boolean result = reservationRepository.existsByTimeId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_테마와_일치하는_예약이_있는지_확인() {
        // when
        boolean result = reservationRepository.existsByThemeId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_날짜_시간_테마와_일치하는_예약이_있는지_확인() {
        // when
        boolean result = reservationRepository.existsByDateAndTimeIdAndThemeId(
                LocalDate.now().minusDays(1), 1L, 1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_날짜_시간_테마_상태와_일치하는_예약이_있는지_확인() {
        // when
        boolean result = reservationRepository.existsByDateAndTimeIdAndThemeIdAndStatus(
                LocalDate.now().minusDays(1), 1L, 1L, Status.RESERVED);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_날짜_시간_테마_멤버와_일치하는_예약이_있는지_확인() {
        // when
        boolean result = reservationRepository.existsByDateAndTimeIdAndThemeIdAndMemberId(
                LocalDate.now().minusDays(1), 1L, 1L, USER_ID);

        // then
        assertThat(result).isTrue();
    }
}
