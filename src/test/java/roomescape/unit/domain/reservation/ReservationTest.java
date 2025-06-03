package roomescape.unit.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import roomescape.integration.fixture.ReservationDateFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberEncodedPassword;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.domain.Reservation;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.time.domain.ReservationTime;

class ReservationTest {

    private final ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
    private final Theme theme = new Theme(
            1L,
            new ThemeName("공포"),
            new ThemeDescription("무섭다"),
            new ThemeThumbnail("thumb.jpg")
    );

    @Test
    void 멤버는_null일_수_없다() {
        Member member = new Member(
                1L,
                new MemberName("홍길동"),
                new MemberEmail("leehyeonsu4888@gmail.com"),
                new MemberEncodedPassword("dsadsa"),
                MemberRole.MEMBER
        );
        ReservationSchedule schedule = new ReservationSchedule(1L, ReservationDateFixture.예약날짜_오늘, time, theme);
        assertThatThrownBy(() -> new Reservation(1L, null, schedule))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void 예약_일정은_null일_수_없다() {
        Member member = new Member(
                1L,
                new MemberName("홍길동"),
                new MemberEmail("leehyeonsu4888@gmail.com"),
                new MemberEncodedPassword("dsadsa"),
                MemberRole.MEMBER
        );
        assertThatThrownBy(() -> new Reservation(1L, member, null))
                .isInstanceOf(NullPointerException.class);
    }

}
