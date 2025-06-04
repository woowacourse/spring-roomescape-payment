package roomescape.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Waiting;

public record MyHistoryResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        BigDecimal amount
) {

    public static MyHistoryResponse ofReservation(final Reservation reservation, final Payment payment) {
        return new MyHistoryResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                "예약",
                payment.getAmount()
        );
    }

    public static MyHistoryResponse ofWaiting(final Waiting waiting, final Long count) {
        return new MyHistoryResponse(
                waiting.getId(),
                waiting.getThemeName(),
                waiting.getDate(),
                waiting.getStartAt(),
                String.format("%d번째 예약대기", count + 1),
                null
        );
    }
}
