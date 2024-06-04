package roomescape.service.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public class ReservationPaymentMapper {
    public static Map<Reservation, Payment> toMap(List<Reservation> reservations, List<Payment> payments) {
        Map<Reservation, Payment> reservationPaymentMap = new HashMap<>();
        for (Reservation reservation : reservations) {
            reservationPaymentMap.put(reservation, payments.stream()
                    .filter(payment -> reservation.equals(payment.getReservation()))
                    .findAny()
                    .orElse(null));
        }
        return reservationPaymentMap;
    }
}
