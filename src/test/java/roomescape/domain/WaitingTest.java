package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WaitingTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDate NEXT_DAY = LocalDate.now().plusDays(1);

    @Nested
    @DisplayName("대기를 생성할 때 검증을 수행한다.")
    class validate {

        @Test
        @DisplayName("비어있는 날짜로는 대기를 생성할 수 없다")
        void cannotCreateBecauseNullDate() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate nullDate = null;

            // when & then
            assertThatThrownBy(() -> Waiting.createWithoutIdWithoutPayment(nullDate, theme, time, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 날짜로 대기를 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 테마로는 대기를 생성할 수 없다")
        void cannotCreateBecauseNullTheme() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme nullTheme = null;
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(() -> Waiting.createWithoutIdWithoutPayment(date, nullTheme, time, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 테마로 대기를 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 시간으로는 대기를 생성할 수 없다")
        void cannotCreateBecauseNullTime() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime nullTime = null;
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(() -> Waiting.createWithoutIdWithoutPayment(date, theme, nullTime, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 시간으로 대기를 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 회원으로는 대기를 생성할 수 없다")
        void cannotCreateBecauseNullMember() {
            // given
            Member nullMember = null;
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(() -> Waiting.createWithoutIdWithoutPayment(date, theme, time, nullMember))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 회원으로 대기를 생성할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("대기 날짜가 지났는지 확인할 수 있다.")
    class isPastWaiting {

        @Test
        void isPastWaiting() {
            // given
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qwer1234!");
            Waiting waiting = Waiting.createWithoutIdWithoutPayment(YESTERDAY, theme, time, member);

            // when
            boolean isPast = waiting.isPastWaiting();

            // then
            assertThat(isPast).isTrue();
        }

        @Test
        void isNotPastWaiting() {
            // given
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qwer1234!");
            Waiting waiting = Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member);

            // when
            boolean isPast = waiting.isPastWaiting();

            // then
            assertThat(isPast).isFalse();
        }
    }
}
