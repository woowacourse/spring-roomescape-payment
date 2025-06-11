package roomescape.booking.dto;

import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationStatus;
import roomescape.booking.waiting.Waiting;
import roomescape.reservationpayment.ReservationPayment;
import roomescape.reservationpayment.dto.ReservationPaymentResponse;
import roomescape.schedule.dto.ScheduleResponse;

public record BookingResponse(
        Long id,
        ScheduleResponse schedule,
        String status,
        ReservationPaymentResponse payment
) {

    public static BookingResponse of(Reservation reservation, ReservationPayment payment) {
        if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
            return new BookingResponse(reservation.getId(), ScheduleResponse.of(reservation.getSchedule()), "예약", ReservationPaymentResponse.from(payment));
        }
        if (reservation.getReservationStatus() == ReservationStatus.PROMOTED) {
            return new BookingResponse(reservation.getId(), ScheduleResponse.of(reservation.getSchedule()), "결제 대기", ReservationPaymentResponse.from(payment));
        }
        return new BookingResponse(reservation.getId(), ScheduleResponse.of(reservation.getSchedule()), "결제 실패", ReservationPaymentResponse.from(payment));
    }

    public static BookingResponse of(Waiting waiting, Long rank) {
        return new BookingResponse(waiting.getId(), ScheduleResponse.of(waiting.getSchedule()), rank + "번째 예약대기", ReservationPaymentResponse.createEmptyReservationPaymentResponse());
    }
}
