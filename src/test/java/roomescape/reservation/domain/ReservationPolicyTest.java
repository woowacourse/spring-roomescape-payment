package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import(DBHelper.class)
class ReservationPolicyTest {

    @Autowired
    DBHelper dbHelper;

    private final ReservationPolicy reservationPolicy = new ReservationPolicy();

    @DisplayName("예약 조건이 유효하면 예외 없이 통과된다")
    @Test
    void validateReservationAvailable_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Reservation reservation = createReservationOf(member, DEFAULT_DATE, time, theme);

        // when & then
        assertDoesNotThrow(() -> reservationPolicy.validateReservationAvailable(reservation, false));
    }

    @DisplayName("중복된 예약이 존재하면 예외가 발생한다")
    @Test
    void validateReservationAvailable_duplicate() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Reservation reservation = createReservationOf(member, DEFAULT_DATE, time, theme);

        // when & then
        assertThatThrownBy(() -> reservationPolicy.validateReservationAvailable(reservation, true))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("이미 해당 날짜에 예약이 존재합니다.");
    }

    @DisplayName("예약 시간이 과거이면 예외가 발생한다")
    @Test
    void validateReservationAvailable_past() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Reservation reservation = createReservationOf(member, pastDate, time, theme);

        // when & then
        assertThatThrownBy(() -> reservationPolicy.validateReservationAvailable(reservation, false))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("지난 날짜와 시간에 대한 예약은 불가능합니다.");
    }
}
