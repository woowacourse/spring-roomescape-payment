package roomescape.domain.waiting;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import roomescape.BaseTest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;

class WaitingTest extends BaseTest {

    @Test
    void 확정된_예약으로_대기번호_생성할_경우_예외() {
        // given
        Reservation reservation = new Reservation(
                LocalDate.now(),
                new ReservationTime(LocalTime.now()),
                new Theme("테마명", "테마설명테마설명테마설명", "썸네일"),
                new Member("이름", "이메일", "비밀번호", Role.USER),
                Status.RESERVED
        );

        // when, then
        assertThatThrownBy(() -> new Waiting(reservation, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
