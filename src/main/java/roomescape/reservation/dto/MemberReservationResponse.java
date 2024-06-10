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
        BigDecimal amount
) {

    public static MemberReservationResponse toResponse(ReservationWithPaymentResponse reservationWithPaymentResponse, int rank) {
        return new MemberReservationResponse(
                reservationWithPaymentResponse.getId(),
                reservationWithPaymentResponse.getTheme().getName(),
                reservationWithPaymentResponse.getDate(),
                reservationWithPaymentResponse.getTime().getStartAt(),
                reservationWithPaymentResponse.getStatus().getDisplayName(),
                rank,
                reservationWithPaymentResponse.getPaymentKey(),
                reservationWithPaymentResponse.getTotalAmount()
        );
    }
}
