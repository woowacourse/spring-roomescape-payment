package roomescape.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.waiting.WaitingResponse;

public record UserReservationPaymentResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    private static final String RESERVED = "예약";
    private static final String WAITING_ORDER = "%d번째 예약 대기";
    private static final String PENDING = "결제 대기";

    public static UserReservationPaymentResponse ofReservations(final Reservation reservation, final Payment payment) {
        return new UserReservationPaymentResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static UserReservationPaymentResponse fromPendings(final Reservation pending) {
        return new UserReservationPaymentResponse(
                pending.getId(),
                pending.getTheme().getThemeName(),
                pending.getDate(),
                pending.getTime().getStartAt(),
                PENDING,
                null,
                null
        );
    }

    public static UserReservationPaymentResponse fromWaiting(final WaitingResponse waiting) {
        return new UserReservationPaymentResponse(
                waiting.waitingId(),
                waiting.theme(),
                waiting.date(),
                waiting.startAt(),
                String.format(WAITING_ORDER, waiting.order()),
                "",
                BigDecimal.ZERO
        );
    }

    public static List<UserReservationPaymentResponse> ofReservations(final List<Reservation> reservations,
                                                                      final List<Payment> payments) {
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();

        for (Reservation reservation : reservations) {
            for (Payment payment : payments) {
                if (payment.isReservation(reservation)) {
                    reservationPaymentResponses.add(ofReservations(reservation, payment));
                }
            }
        }

        return reservationPaymentResponses;
    }

    public static List<UserReservationPaymentResponse> fromPendings(final List<Reservation> pendings) {
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();

        for (Reservation pending : pendings) {
            reservationPaymentResponses.add(fromPendings(pending));
        }

        return reservationPaymentResponses;
    }

    public static List<UserReservationPaymentResponse> fromWaitings(final List<WaitingResponse> waitings) {
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();

        for (WaitingResponse waitingResponse : waitings) {
            reservationPaymentResponses.add(fromWaiting(waitingResponse));
        }

        return reservationPaymentResponses;
    }
}
