package roomescape.reservationslot.presentation.dto.response;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationslot.domain.ReservationSlot;

public record MyReservationResponse(Long reservationSlotId,
                                    String theme,
                                    String date,
                                    String time,
                                    boolean isReserved,
                                    long waitingRank,
                                    String paymentKey,
                                    long amount) {

    public static MyReservationResponse from(final Reservation reservation) {
        ReservationSlot reservationSlot = reservation.getReservationSlot();
        Payment payment = reservation.getPayment();
        Long slotId = reservationSlot.getId();
        String themeName = reservationSlot.getTheme().getName();
        String reservationSlotDate = reservationSlot.getDate().toString();
        String startAt = reservationSlot.getTime().getStartAt().toString();
        boolean isReserved = reservation.isReserved();
        long rank = reservationSlot.findRank(reservation);
        return new MyReservationResponse(
                slotId,
                themeName,
                reservationSlotDate,
                startAt,
                isReserved,
                rank,
                getPaymentKey(payment),
                getAmount(payment)
        );
    }

    private static String getPaymentKey(final Payment payment) {
        if (payment != null) {
            return payment.getPaymentKey();
        }
        return null;
    }

    private static Long getAmount(final Payment payment) {
        if (payment != null) {
            return payment.getAmount();
        }
        return 0L;
    }
}
