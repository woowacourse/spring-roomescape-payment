package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createMemberByName;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;
import static roomescape.TestFixture.createWaitingOf;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@Import({WaitingValidator.class, DBHelper.class})
class WaitingValidatorTest {

    @Autowired
    DBHelper dbHelper;

    @Autowired
    WaitingValidator waitingValidator;

    @Nested
    @DisplayName("대기 등록이 가능한지 검증")
    class CanRegisterWaiting {

        @DisplayName("대기 등록이 가능하다면 예외 없이 통과")
        @Test
        void validateCanRegisterWaiting_success() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            WaitingReservation waiting = createWaitingOf(member, DEFAULT_DATE, time, theme);

            // when & then
            assertDoesNotThrow(() -> waitingValidator.validateCanRegisterWaiting(waiting));
        }

        @DisplayName("과거 날짜에 대한 대기 시도 시 예외 발생")
        @Test
        void validateCanRegisterWaiting_past() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            LocalDate pastDate = LocalDate.now().minusDays(1);
            WaitingReservation waiting = createWaitingOf(member, pastDate, time, theme);

            // when & then
            assertThatThrownBy(() -> waitingValidator.validateCanRegisterWaiting(waiting))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("지난 날짜에 대한 대기입니다.");
        }

        @DisplayName("동일한 슬롯에 대해 이미 자신의 예약이 있는데도 대기를 시도할 경우 예외 발생")
        @Test
        void validateCanRegisterWaiting_alreadyHasReservation() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            
            dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
            WaitingReservation waiting = createWaitingOf(member, DEFAULT_DATE, time, theme);

            // when & then
            assertThatThrownBy(() -> waitingValidator.validateCanRegisterWaiting(waiting))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("사용자는 이미 해당 날짜에 예약 또는 대기했습니다.");
        }

        @DisplayName("동일한 슬롯에 대해 이미 자신의 대기가 있는데도 대기 시도할 경우 예외 발생")
        @Test
        void validateCanRegisterWaiting_alreadyHasWaiting() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            
            dbHelper.insertWaiting(createWaitingOf(member, DEFAULT_DATE, time, theme));
            WaitingReservation waiting = createWaitingOf(member, DEFAULT_DATE, time, theme);

            // when & then
            assertThatThrownBy(() -> waitingValidator.validateCanRegisterWaiting(waiting))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("사용자는 이미 해당 날짜에 예약 또는 대기했습니다.");
        }
    }

    @Nested
    @DisplayName("대기 승인이 가능한지 검증")
    class ApproveWaiting {

        @DisplayName("대기 승인이 가능하다면 예외 없이 통과")
        @Test
        void validateCanApproveWaiting_success() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            WaitingReservation waiting = createWaitingOf(member, DEFAULT_DATE, time, theme);

            // when & then
            assertDoesNotThrow(() -> waitingValidator.validateCanApproveWaiting(waiting));
        }

        @DisplayName("과거 날짜에 대한 대기를 승인할 시 예외 발생")
        @Test
        void validateCanRegisterWaiting_past() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            LocalDate pastDate = LocalDate.now().minusDays(1);
            WaitingReservation waiting = createWaitingOf(member, pastDate, time, theme);

            // when & then
            assertThatThrownBy(() -> waitingValidator.validateCanApproveWaiting(waiting))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("지난 날짜에 대한 대기입니다.");
        }

        @DisplayName("이미 동일한 슬롯에 예약이 존재할 경우 예외 발생")
        @Test
        void validateCanApproveWaiting_alreadyHasReservation() {
            // given
            Member member1 = dbHelper.insertMember(createDefaultMember_1());
            Member member2 = dbHelper.insertMember(createMemberByName("회원2"));
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            
            dbHelper.insertReservation(createReservationOf(member1, DEFAULT_DATE, time, theme));
            WaitingReservation waiting = createWaitingOf(member2, DEFAULT_DATE, time, theme);

            // when & then
            assertThatThrownBy(() -> waitingValidator.validateCanApproveWaiting(waiting))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("이미 해당 날짜에 예약이 존재합니다.");
        }
    }
}
