package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.model.ReservationTicket;
import roomescape.model.TossPayment;

public record MemberReservationResponseDto(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String paymentKey,
        Long amount
) {
    public MemberReservationResponseDto(ReservationTicket reservationTicket, TossPayment tossPayment) {
        this(
                reservationTicket.getId(),
                reservationTicket.getTheme().getName(),
                reservationTicket.getDate(),
                reservationTicket.getReservationTime().getStartAt(),
                tossPayment.getPaymentKey(),
                tossPayment.getAmount()
        );
    }
}
