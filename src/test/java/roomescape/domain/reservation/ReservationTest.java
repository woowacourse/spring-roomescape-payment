package roomescape.domain.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roomescape.TestFixture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static roomescape.TestFixture.ADMIN;
import static roomescape.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.TestFixture.DATE_MAY_NINTH;
import static roomescape.TestFixture.PAYMENT_KEY;
import static roomescape.TestFixture.RESERVATION_TIME_SIX;
import static roomescape.TestFixture.START_AT_SEVEN;
import static roomescape.TestFixture.START_AT_SIX;
import static roomescape.TestFixture.THEME_COMIC;

class ReservationTest {

    private static Stream<Arguments> reservationsAndExpectedResult() {
        return Stream.of(
                Arguments.of(DATE_MAY_EIGHTH, START_AT_SIX, true),
                Arguments.of(DATE_MAY_NINTH, START_AT_SEVEN, false)
        );
    }

    @Test
    @DisplayName("예약이 생성된다.")
    void createReservation() {
        assertThatCode(() -> new Reservation(TestFixture.MEMBER_CAT(), DATE_MAY_EIGHTH, RESERVATION_TIME_SIX(), TestFixture.THEME_COMIC(), PAYMENT_KEY,1_000))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("reservationsAndExpectedResult")
    @DisplayName("예약이 동일한 예약 시간을 갖는지 확인한다.")
    void hasSameDateTime(final LocalDate date, final String time, final boolean expectedResult) {
        // given
        final Reservation reservation = new Reservation(TestFixture.MEMBER_CAT(), DATE_MAY_EIGHTH, RESERVATION_TIME_SIX(), TestFixture.THEME_COMIC(), PAYMENT_KEY,1_000);

        // when
        final boolean actual = reservation.hasSameDateTime(date, new ReservationTime(time));

        // then
        assertThat(actual).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("예약이 가능한 날짜와 시간이라면 참이다.")
    void isAvailable_True() {
        // given
        Reservation reservation = new Reservation(ADMIN(), LocalDate.now(), new ReservationTime(0L, LocalTime.now().plusHours(1)), THEME_COMIC(), PAYMENT_KEY,1_000);

        // when
        boolean available = reservation.isAvailable();

        // then
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("예약이 불가능한 날짜와 시간이라면 거짓이다.")
    void isAvailableDate_False() {
        // given
        Reservation reservation = new Reservation(ADMIN(), LocalDate.now(), new ReservationTime(0L, LocalTime.now().minusHours(1)), THEME_COMIC(), PAYMENT_KEY,1_000);

        // when
        boolean available = reservation.isAvailable();

        // then
        assertThat(available).isFalse();
    }
}
