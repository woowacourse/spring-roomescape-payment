package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.HOUR_10;
import static roomescape.util.Fixture.HOUR_11;
import static roomescape.util.Fixture.JOJO_EMAIL;
import static roomescape.util.Fixture.JOJO_NAME;
import static roomescape.util.Fixture.JOJO_PASSWORD;
import static roomescape.util.Fixture.KAKI_EMAIL;
import static roomescape.util.Fixture.KAKI_NAME;
import static roomescape.util.Fixture.KAKI_PASSWORD;
import static roomescape.util.Fixture.TODAY;
import static roomescape.util.Fixture.TOMORROW;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.auth.domain.Role;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;
import roomescape.reservation.dto.ReservationWithPaymentResponse;

class WaitingsTest {

    @DisplayName("대기 상태의 예약 중 동일한 예약에 대한 대기 순서를 구한다.")
    @Test
    void findMemberRank() {
        Theme theme = new Theme(1L, new ThemeName(HORROR_THEME.getName()), new Description(HORROR_THEME.getDescription()), HORROR_THEME.getThumbnail());

        ReservationTime hourTen = new ReservationTime(1L, HOUR_10);
        ReservationTime hourEleven = new ReservationTime(2L, HOUR_11);

        Member kaki = new Member(1L, Role.USER, new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD);
        Member jojo = new Member(2L, Role.USER, new MemberName(JOJO_NAME), JOJO_EMAIL, JOJO_PASSWORD);

        Reservation kakiReservation1 = new Reservation(kaki, TODAY, theme, hourTen, ReservationStatus.WAIT);
        Reservation kakiReservation2 = new Reservation(kaki, TOMORROW, theme, hourTen, ReservationStatus.WAIT);
        Reservation jojoReservation1 = new Reservation(jojo, TODAY, theme, hourTen, ReservationStatus.WAIT);
        Reservation jojoReservation2 = new Reservation(jojo, TODAY, theme, hourEleven, ReservationStatus.WAIT);

        Waitings waitings = new Waitings(List.of(kakiReservation1, kakiReservation2, jojoReservation1, jojoReservation2));

        ReservationWithPaymentResponse reservationWithPaymentResponse
                = new ReservationWithPaymentResponse(1L, theme, TODAY, hourTen, ReservationStatus.WAIT, "", new BigDecimal("0"));

        assertAll(
                () -> assertThat(waitings.findMemberRank(reservationWithPaymentResponse, kaki.getId())).isEqualTo(1),
                () -> assertThat(waitings.findMemberRank(reservationWithPaymentResponse, jojo.getId())).isEqualTo(2)
        );
    }
}
