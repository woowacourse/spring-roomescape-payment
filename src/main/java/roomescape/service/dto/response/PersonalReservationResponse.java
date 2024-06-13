package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationwaiting.WaitingWithRank;

import java.time.LocalDate;
import java.time.LocalTime;

public record PersonalReservationResponse(
        Long id,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String theme,
        String status,
        PaymentResponse payment
) {

    public static PersonalReservationResponse from(Reservation reservation, Payment payment) {
        return new PersonalReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getTheme().getRawName(),
                "예약",
                PaymentResponse.from(payment)
        );
    }

    public static PersonalReservationResponse from(WaitingWithRank waitingWithRank) {
        return new PersonalReservationResponse(
                waitingWithRank.id(),
                waitingWithRank.date(),
                waitingWithRank.time(),
                waitingWithRank.themeName(),
                String.format("%d번째 예약 대기", waitingWithRank.rank()),
                PaymentResponse.empty()
        );
    }
}
