package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDate NEXT_DAY = LocalDate.now().plusDays(1);

    @Nested
    @DisplayName("예약을 생성할 때 검증을 수행한다.")
    class validate {

        @Test
        @DisplayName("비어있는 예약날짜로는 예약을 생성할 수 없다")
        void cannotCreateBecauseNullDate() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate nullDate = null;

            // when & then
            assertThatThrownBy(() -> Reservation.createWithoutIdAndPaymentHistory(nullDate, time, theme, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 예약날짜로 예약을 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 예약시간으로는 예약을 생성할 수 없다")
        void cannotCreateBecauseNullTime() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime nullTime = null;
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(() -> Reservation.createWithoutIdAndPaymentHistory(date, nullTime, theme, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 예약시간으로는 예약을 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 테마로는 예약을 생성할 수 없다")
        void cannotCreateBecauseNullTheme() {
            // given
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qweqw123!");
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme nullTheme = null;
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(
                    () -> Reservation.createWithoutIdAndPaymentHistory(date, time, nullTheme, member))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 테마로는 예약을 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("비어있는 멤버로는 예약을 생성할 수 없다")
        void cannotCreateBecauseNullMember() {
            // given
            Member nullMember = null;
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            LocalDate date = LocalDate.now();

            // when & then
            assertThatThrownBy(
                    () -> Reservation.createWithoutIdAndPaymentHistory(date, time, theme, nullMember))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 멤버로는 예약을 생성할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("예약 날짜가 지났는지 확인할 수 있다.")
    class isPastDateTime {

        @Test
        void isPastDateTime() {
            // given
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qwer1234!");
            Reservation reservation = Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member);

            // when
            boolean isPast = reservation.isPastDateTime();

            // then
            assertThat(isPast).isTrue();
        }

        @Test
        void isNotPastDateTime() {
            // given
            ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
            Theme theme = Theme.createWithoutId("회원", "설명", "섬네일");
            Member member = Member.createWithoutId(Role.GENERAL, "회원", "test@test.com", "qwer1234!");
            Reservation reservation = Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, member);

            // when
            boolean isPast = reservation.isPastDateTime();

            // then
            assertThat(isPast).isFalse();
        }
    }
}
