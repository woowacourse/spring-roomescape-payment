package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import roomescape.member.domain.Email;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Password;
import roomescape.member.domain.Role;
import roomescape.theme.domain.Theme;

class WaitingTest {

    @Test
    void 대기_상태를_변경한다() {
        // given
        final ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
        final Theme theme = new Theme(1L, "인터스텔라", "설명1", "썸네일1");
        final Member member = Member.ofMember("엠제이", "", "");
        final Waiting waiting = new Waiting(LocalDate.of(2025, 1, 1), time, theme, member);

        // when & then
        assertAll(
            // 초기 상태는 PENDING
            () -> assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.PENDING),

            // 취소 후 상태는 CANCELLED
            () -> {
                waiting.cancel();
                assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.CANCELLED);
            },

            // 거절 후 상태는 REJECTED
            () -> {
                waiting.reject();
                assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.REJECTED);
            },

            // 수락 후 상태는 ACCEPTED
            () -> {
                waiting.accept();
                assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.ACCEPTED);
            }
        );
    }

    @Test
    void 대기_소유자를_확인한다() {
        // given
        final ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
        final Theme theme = new Theme(1L, "인터스텔라", "설명1", "썸네일1");
        final Member member = new Member(1L, new MemberName("엠제이"), new Email(""), new Password(""), Role.MEMBER);
        final Waiting waiting = new Waiting(LocalDate.of(2025, 1, 1), time, theme, member);

        // when & then
        assertAll(
            () -> assertThat(waiting.isOwner(1L)).isTrue(),
            () -> assertThat(waiting.isOwner(2L)).isFalse()
        );
    }

    @Test
    void 테마_이름과_시작_시간을_조회한다() {
        // given
        final LocalTime startTime = LocalTime.of(10, 0);
        final ReservationTime time = new ReservationTime(1L, startTime);
        final Theme theme = new Theme(1L, "인터스텔라", "설명1", "썸네일1");
        final Member member = Member.ofMember("엠제이", "", "");
        final Waiting waiting = new Waiting(LocalDate.of(2025, 1, 1), time, theme, member);

        // when & then
        assertAll(
            () -> assertThat(waiting.getThemeName()).isEqualTo("인터스텔라"),
            () -> assertThat(waiting.getStartAt()).isEqualTo(startTime)
        );
    }
}
