package roomescape.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.utils.TestFixture;

class ReservationTest {
    private static final Member member = TestFixture.getAdmin();
    private static final Theme theme = TestFixture.getTheme("테마");
    private static final ReservationTime time = TestFixture.getReservationTimeAfterMinute(1);
    private static final ReservationTime pastTime = TestFixture.getReservationTimeBeforeMinute(1);

    @Test
    @DisplayName("예약 날짜를 저장할 때, 문자열을 LocalDate 타입으로 변환한다.")
    void parseDate() {
        final String date = "2022-12-31";
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.BOOKED);

        assertThat(reservation.getDate()).isEqualTo(LocalDate.of(2022, 12, 31));
    }

    @Test
    @DisplayName("예약 날짜 형식이 올바르지 않을 경우 예외가 발생한다.")
    void parseDateWithInvalidFormat() {
        final String date = "2222222222";

        assertThatThrownBy(() -> new Reservation(member, date, time, theme, ReservationStatus.BOOKED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Reservation.DATE_FORMAT_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("예약 날짜가 현재 날짜보다 이전인지 확인할 수 있다.")
    void isDatePast() {
        final String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.BOOKED);

        assertThatThrownBy(reservation::validateDateAndTime)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Reservation.PAST_DATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("예약 날짜가 오늘이지만 지난 시간인지 확인할 수 있다.")
    void isDateTodayButTimePast() {
        final String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        final Reservation reservation = new Reservation(member, date, pastTime, theme, ReservationStatus.BOOKED);

        assertThatThrownBy(reservation::validateDateAndTime)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Reservation.PAST_TIME_EXCEPTION_MESSAGE);
    }
}
