package roomescape.mvc.waiting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.test.fixture.DateFixture.TODAY;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;

class WaitingTest {

    @Nested()
    @DisplayName("과거 대기 데이터인지 확인할 수 있다.")
    public class isPastWaiting {

        @DisplayName("과거 대기 데이터인 경우 false 리턴")
        @Test
        void checkPastWaiting() {
            // given
            Theme theme = new Theme(1L, "테마", "설명", "섬네일");
            ReservationTime time = new ReservationTime(1L, LocalTime.now().minusSeconds(1));
            Member member = new Member(1L, Role.GENERAL, "회원", "test@email.com", "qwer1234!");
            Waiting pastWaiting = Waiting.createWithoutIdWithoutPayment(TODAY, theme, time, member);

            // when
            boolean isPast = pastWaiting.isPastWaiting();

            // then
            assertThat(isPast).isTrue();
        }

        @DisplayName("과거 대기 데이터가 아닌 경우 true 리턴")
        @Test
        void checkNotPastWaiting() {
            // given
            Theme theme = new Theme(1L, "테마", "설명", "섬네일");
            ReservationTime time = new ReservationTime(1L, LocalTime.now().plusSeconds(1));
            Member member = new Member(1L, Role.GENERAL, "회원", "test@email.com", "qwer1234!");
            Waiting pastWaiting = Waiting.createWithoutIdWithoutPayment(TODAY, theme, time, member);

            // when
            boolean isPast = pastWaiting.isPastWaiting();

            // then
            assertThat(isPast).isFalse();
        }
    }
}
