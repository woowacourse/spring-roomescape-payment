package roomescape.booking.dto;

import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationPaymentStatus;
import roomescape.booking.reservation.dto.ReservationPayment;
import roomescape.booking.waiting.Waiting;
import roomescape.payment.Payment;
import roomescape.schedule.dto.ScheduleResponse;

public record BookingResponse(
        Long id,
        ScheduleResponse schedule,
        String status,
        String paymentKey,
        Long amount
) {

    public static BookingResponse of(ReservationPayment reservationPayment) {
        Reservation reservation = reservationPayment.reservation();
        Payment payment = reservationPayment.payment();
        return new BookingResponse(
                reservation.getId(),
                ScheduleResponse.of(reservation.getSchedule()),
                createDisplayStatus(reservationPayment.paymentStatus()),
                payment.getPaymentKey(),
                payment.getAmount());
    }

    private static String createDisplayStatus(ReservationPaymentStatus status) {
        if (status == ReservationPaymentStatus.PENDING) {
            return "결제 대기";
        }
        return "예약";
    }

    public static BookingResponse of(Waiting waiting, Long rank) {
        return new BookingResponse(
                waiting.getId(),
                ScheduleResponse.of(waiting.getSchedule()), rank + "번째 예약대기",
                null,
                null);
    }
}
