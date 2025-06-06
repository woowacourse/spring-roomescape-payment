package roomescape.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyReservation(
    Long id,
    String theme,
    LocalDate date,
    @JsonFormat(pattern = "HH:mm")
    LocalTime time,
    String status,
    String paymentKey,
    Long amount
) {

    static final String WAITING_STATUS = "%s번째 예약대기";

    public static MyReservation from(Reservation reservation) {
        return new MyReservation(
            reservation.getId(),
            reservation.getThemeName(),
            reservation.getDate(),
            reservation.getStartAt(),
            reservation.getStatus().getTitle(),
            reservation.getPayment() != null ? reservation.getPayment().getPaymentInfo().getPaymentKey() : null, // NOTE. 가독성이 떨어지는지 여쭤보기
            reservation.getPayment() != null ? reservation.getPayment().getPaymentInfo().getAmount() : null
        );
    }

    public static MyReservation from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.getWaiting();
        return new MyReservation(
            waiting.getId(),
            waiting.getThemeName(),
            waiting.getDate(),
            waiting.getReservationStartAt(),
            WAITING_STATUS.formatted(waitingWithRank.getRank()),
            null,
            null
        );
    }
}
