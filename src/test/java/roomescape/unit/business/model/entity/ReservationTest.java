package roomescape.unit.business.model.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.exception.reservation.PastDateReservationException;
import roomescape.exception.reservation.TooFarDateReservationException;

class ReservationTest {

    private static final LocalDate DATE = LocalDate.now().plusDays(5);
    private static final TimeSlot RESERVATION_TIME = TimeSlot.create(LocalTime.of(10, 0));
    private static final Theme THEME = Theme.create("공포", "", "");
    private static final String NAME = "dompoo";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password1234!";

    @Nested
    class 생성_테스트 {

        @Test
        void 정상적인_예약을_생성할_수_있다() {
            // given
            final Member member = Member.create(NAME, EMAIL, PASSWORD);

            // when
            final Reservation reservation = Reservation.create(member, DATE, RESERVATION_TIME, THEME);
            // then
            assertThat(reservation).isNotNull();
            assertThat(reservation.getMember().getName().value()).isEqualTo(NAME);
            assertThat(reservation.getDate().value()).isEqualTo(DATE);
            assertThat(reservation.getTimeSlot()).isEqualTo(RESERVATION_TIME);
            assertThat(reservation.getTheme()).isEqualTo(THEME);
        }

        @Test
        void 과거_날짜로_예약할_수_없다() {
            final LocalDate pastDate = LocalDate.now().minusDays(1);
            final Member member = Member.create(NAME, EMAIL, PASSWORD);

            assertThatThrownBy(
                    () -> Reservation.create(member, pastDate, RESERVATION_TIME, THEME))
                    .isInstanceOf(PastDateReservationException.class);
        }

        @Test
        void 현재보다_일주일_이후로_예약할_수_없다() {
            final LocalDate over7DaysDate = LocalDate.now().plusDays(8);
            final Member member = Member.create(NAME, EMAIL, PASSWORD);

            assertThatThrownBy(
                    () -> Reservation.create(member, over7DaysDate, RESERVATION_TIME, THEME))
                    .isInstanceOf(TooFarDateReservationException.class);
        }
    }
}
