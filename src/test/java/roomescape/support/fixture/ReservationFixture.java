package roomescape.support.fixture;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.support.FakePaymentClient;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationFixture {

    public static Reservation create(Member member, ReservationTime time, Theme theme) {
        return create("2024-05-23", member, time, theme);
    }

    public static Reservation create(String date, Member member, ReservationTime time, Theme theme) {
        return create(LocalDate.parse(date), member, time, theme);
    }

    public static Reservation create(LocalDate date, Member member, ReservationTime time, Theme theme) {
        return new Reservation(date, member, time, theme);
    }

    public static ReservationCreateRequest createValidRequest(Long memberId, Long timeId, Long themeId) {
        return createRequest(memberId, timeId, themeId, "testPaymentKey");
    }

    public static ReservationCreateRequest createInvalidRequest(Long memberId, Long timeId, Long themeId) {
        return createRequest(memberId, timeId, themeId, FakePaymentClient.PAYMENT_ERROR_KEY);
    }

    private static ReservationCreateRequest createRequest(Long memberId, Long timeId, Long themeId, String paymentKey) {
        LocalDate date = LocalDate.parse("2024-06-23");
        return new ReservationCreateRequest(date, timeId, themeId, memberId, paymentKey, "testId", BigDecimal.TEN);
    }
}
