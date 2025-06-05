package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.waiting.ReservationWaitingRank;

public record MyReservationResponseDto(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String statusMessage,
        String paymentKey,
        Long amount
) {

    //TODO 생성자가 너무 불명확해서 용도를 이름으로 드러내줘야 할듯
    public MyReservationResponseDto(Reservation reservation, Payment payment) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getMessage(),
                payment.getPaymentKey(),
                payment.getTotalAmount()
        );
    }

    public MyReservationResponseDto(Reservation reservation, ReservationWaitingRank rank) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                rank.getStatusMessage(),
                null,
                null
        );
    }
}
