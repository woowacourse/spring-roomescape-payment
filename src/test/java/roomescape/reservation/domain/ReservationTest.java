package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.fixture.DateFixture;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class ReservationTest {
    private final Member member = new Member(1L, "커찬", "kuchn@abc.com");
    private final LocalDate date = LocalDate.of(2050, 10, 10);
    private final ReservationTime time = new ReservationTime(LocalTime.of(9, 0));
    private final Theme theme = new Theme(
            "오리와 호랑이",
            "오리들과 호랑이들 사이에서 살아남기",
            "https://image.jpg");

    @DisplayName("예약자가 비어있을 때 예외를 던진다.")
    @Test
    void validateReservationTest_whenNameIsNull() {
        assertThatThrownBy(() ->
                new Reservation(1L, null, date, time, theme, ReservationStatus.RESERVED))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("날짜 비어있을 때 예외를 던진다.")
    @Test
    void validateReservationTest_whenDateIsNull() {
        assertThatThrownBy(() ->
                new Reservation(1L, member, null, time, theme, ReservationStatus.RESERVED))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("시간 비어있을 때 예외를 던진다.")
    @Test
    void validateReservationTest_whenTimeIsNull() {
        assertThatThrownBy(() ->
                new Reservation(1L, member, date, null, theme, ReservationStatus.RESERVED))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("테마 비어있을 때 예외를 던진다.")
    @Test
    void validateReservationTest_whenThemeIsNull() {
        assertThatThrownBy(() ->
                new Reservation(1L, member, date, time, null, ReservationStatus.RESERVED))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("날짜를 통해 특정 시간대 이전일 경우 예외를 발생한다.")
    @Test
    void isAfterTest_whenDateIsBefore() {
        ReservationTime time = new ReservationTime(1L, LocalTime.of(9, 0));

        assertThatThrownBy(() -> new Reservation(1L, member, DateFixture.BEFORE_DATE, time, theme,
                ReservationStatus.RESERVED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약은 현재 시간 이후여야 합니다.");
    }
}
