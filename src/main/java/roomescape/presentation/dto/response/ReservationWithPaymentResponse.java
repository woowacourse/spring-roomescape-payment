package roomescape.presentation.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;

@Getter
@RequiredArgsConstructor
public class ReservationWithPaymentResponse {

    private final String id;
    private final MemberResponse user;
    private final LocalDate date;
    private final TimeSlotResponse time;
    private final ThemeResponse theme;
    private final PaymentResponse payment;

    public ReservationWithPaymentResponse(Reservation reservation, Payment payment) {
        this.id = reservation.getId().value();
        this.user = MemberResponse.from(reservation.getMember());
        this.date = reservation.getDate().value();
        this.time = TimeSlotResponse.from(reservation.getTimeSlot());
        this.theme = ThemeResponse.from(reservation.getTheme());
        this.payment = PaymentResponse.from(payment);
    }
}
