package roomescape.reservation.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.dto.ReservationWithPayment;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.dto.WaitingInfoDataResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MyReservationsResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        Long rank,
        PaymentResponse payment
) {

    public static MyReservationsResponse from(ReservationWithPayment response) {
        Reservation reservation = response.reservation();
        ReservationStatus reservationStatus = getSavedReservationStatus(reservation);
        return new MyReservationsResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservationStatus.getDescription(),
                null,
                PaymentResponse.from(response.payment())
        );
    }

    private static ReservationStatus getSavedReservationStatus(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            return ReservationStatus.DONE;
        }
        return ReservationStatus.CONFIRMED;
    }

    public static MyReservationsResponse from(WaitingInfoDataResponse waitingInfoDataResponse) {
        Waiting waiting = waitingInfoDataResponse.waiting();
        return new MyReservationsResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                ReservationStatus.WAITING.getDescription(),
                waitingInfoDataResponse.rank().value(),
                null
        );
    }
}
