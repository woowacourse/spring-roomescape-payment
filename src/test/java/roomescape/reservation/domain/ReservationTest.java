package roomescape.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roomescape.member.domain.Member;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_ADMIN;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

class ReservationTest {
    @ParameterizedTest
    @MethodSource(value = "reservationDate")
    @DisplayName("예약자 날짜가 당일 혹은 이전인지 확인한다.")
    void validateName(LocalDate date, boolean expectedResult) {
        // given
        Reservation reservation = new Reservation(
                USER_MIA(), date, new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), BOOKING);

        // when
        boolean actualResult = reservation.isBeforeOrOnToday(MIA_RESERVATION_DATE);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> reservationDate() {
        return Stream.of(
                Arguments.of(MIA_RESERVATION_DATE.minusDays(1), true),
                Arguments.of(MIA_RESERVATION_DATE, true),
                Arguments.of(MIA_RESERVATION_DATE.plusDays(1), false)
        );
    }

    @ParameterizedTest
    @MethodSource(value = "membersToModify")
    @DisplayName("수정 권한을 확인한다.")
    void hasSameOwner(Member memberToModify) {
        // given
        Reservation reservation = new Reservation(
                USER_MIA(1L), MIA_RESERVATION_DATE, new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), BOOKING);

        // when
        boolean hasSameOwner = reservation.isModifiableBy(memberToModify);

        // then
        assertThat(hasSameOwner).isTrue();
    }

    private static Stream<Member> membersToModify() {
        return Stream.of(USER_MIA(1L), USER_ADMIN(2L));
    }
}
