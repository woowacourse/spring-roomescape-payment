package roomescape.reservation.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
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

    private enum ReservationResponseStatus {
        WAITING("대기"),
        CONFIRMED("예약"),
        DONE("완료"),
        ;

        private final String description;

        ReservationResponseStatus(String description) {
            this.description = description;
        }

        public static ReservationResponseStatus from(Reservation reservation) {
            if (reservation.isBefore(LocalDateTime.now())) {
                return DONE;
            }
            return CONFIRMED;
        }

        public String getDescription() {
            return description;
        }
    }

    public static MyReservationsResponse from(ReservationWithPayment response) {
        Reservation reservation = response.reservation();
        ReservationResponseStatus reservationStatus = getSavedReservationStatus(reservation);
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

    private static ReservationResponseStatus getSavedReservationStatus(Reservation reservation) {
        return ReservationResponseStatus.from(reservation);
    }

    public static MyReservationsResponse from(WaitingInfoDataResponse waitingInfoDataResponse) {
        Waiting waiting = waitingInfoDataResponse.waiting();
        return new MyReservationsResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                ReservationResponseStatus.WAITING.getDescription(),
                waitingInfoDataResponse.rank().value(),
                null
        );
    }
}
