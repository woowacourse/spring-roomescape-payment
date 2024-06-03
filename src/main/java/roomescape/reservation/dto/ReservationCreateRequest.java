package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.paymenthistory.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public record ReservationCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        int amount,
        PaymentType paymentType
) {
    public Reservation makeReservation(Member member, ReservationTime time, Theme theme) {
        return new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
    }
}
