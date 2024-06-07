package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import roomescape.reservation.domain.dto.WaitingReservationRanking;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyReservationResponse(Long reservationId,
                                    String theme,
                                    LocalDate date,
                                    LocalTime time,
                                    String status,
                                    String paymentKey,
                                    String amount
) {

    public static MyReservationResponse from(MemberReservation memberReservation) {
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                null,
                null
        );
    }

    public static MyReservationResponse of(MemberReservation memberReservation,
                                           String plainPaymentKey,
                                           BigDecimal amount
    ) {
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                plainPaymentKey,
                amount.toPlainString()
        );
    }

    public static MyReservationResponse from(WaitingReservationRanking waitingReservationRanking) {
        MemberReservation memberReservation = waitingReservationRanking.getMemberReservation();
        String status = waitingReservationRanking.getDisplayRank() + "번째 " + memberReservation.getStatus().getStatusName();
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                status,
                null,
                null
        );
    }
}
