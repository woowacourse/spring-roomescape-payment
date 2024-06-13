package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.member.Member;
import roomescape.domain.member.Name;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.dto.MemberResponse;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.theme.ThemeResponse;

import java.time.LocalDate;

public record ReservationSaveRequest(
        Long memberId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        Long timeId,
        Long themeId
) {

    public Reservation toReservation(final MemberResponse memberResponse,
                                     final ThemeResponse themeResponse,
                                     final ReservationTimeResponse timeResponse,
                                     final PaymentResponse paymentResponse
    ) {
        final Member member = new Member(memberResponse.id(), new Name(memberResponse.name()), memberResponse.email());
        final ReservationTime time = new ReservationTime(timeResponse.id(), timeResponse.startAt());
        final Theme theme = new Theme(themeResponse.id(), themeResponse.name(), themeResponse.description(), themeResponse.thumbnail());
        final Payment payment = new Payment(paymentResponse.paymentKey(), paymentResponse.totalAmount());
        return new Reservation(member, date, time, theme, payment);
    }

    public Reservation toReservation(final MemberResponse memberResponse,
                                     final ThemeResponse themeResponse,
                                     final ReservationTimeResponse timeResponse
    ) {
        return toReservation(memberResponse, themeResponse, timeResponse, new PaymentResponse("결제 필요없음", 0));
    }

    public Waiting toWaiting(final MemberResponse memberResponse,
                             final ThemeResponse themeResponse,
                             final ReservationTimeResponse timeResponse
    ) {
        final Member member = new Member(memberResponse.id(), new Name(memberResponse.name()), memberResponse.email());
        final ReservationTime time = new ReservationTime(timeResponse.id(), timeResponse.startAt());
        final Theme theme = new Theme(themeResponse.id(), themeResponse.name(), themeResponse.description(), themeResponse.thumbnail());
        return new Waiting(member, date, time, theme);
    }
}
