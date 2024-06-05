package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record MemberReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        int rank,
        String paymentKey,
        BigDecimal totalAmount
) {

    public static MemberReservationResponse toResponse(ReservationWithPayment reservationWithPayment, int rank) {
        return new MemberReservationResponse(
                reservationWithPayment.getReservationId(),
                reservationWithPayment.getThemeName(),
                reservationWithPayment.getDate(),
                reservationWithPayment.getStartAt(),
                reservationWithPayment.getStatusDisplayName(),
                rank,
                reservationWithPayment.paymentKey(),
                reservationWithPayment.totalAmount()
        );
    }
}
