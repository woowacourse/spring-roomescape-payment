package roomescape.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;

public record CanceledReservationResponse(
        Long id,
        String theme,
        String member,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal totalAmount
) {
    public static CanceledReservationResponse of(Reservation reservation, String paymentKey, BigDecimal totalAmount) {
        return new CanceledReservationResponse(
                reservation.getId(), reservation.getTheme().getName(), reservation.getMember().getName(),
                reservation.getDate(), reservation.getTime().getStartAt(), reservation.getStatus().getValue(),
                paymentKey, totalAmount
        );
    }
}
