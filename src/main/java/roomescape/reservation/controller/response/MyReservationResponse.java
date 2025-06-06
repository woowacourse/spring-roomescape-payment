package roomescape.reservation.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.toss.domain.TossPayment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.WaitingWithRank;

public record MyReservationResponse(Long id,
                                    String theme,
                                    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                    @JsonFormat(pattern = "HH:mm") LocalTime time,
                                    String status,
                                    MyPaymentInfo paymentInfo) {

    public static MyReservationResponse from(Reservation reservation, TossPayment tossPayment) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getStartAt(),
                "예약",
                MyPaymentInfo.from(tossPayment)
        );
    }

    public static MyReservationResponse from(WaitingWithRank waitingWithRank) {
        Reservation waiting = waitingWithRank.waiting();
        return new MyReservationResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getStartAt(),
                String.valueOf(waitingWithRank.rank()),
                null
        );
    }

}
