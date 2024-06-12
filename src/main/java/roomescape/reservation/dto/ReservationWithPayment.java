package roomescape.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;

public record ReservationWithPayment(Reservation reservation, String paymentKey, BigDecimal totalAmount) {

    public Long getReservationId() {
        return reservation.getId();
    }

    public String getThemeName() {
        return reservation.getThemeName();
    }

    public LocalDate getDate() {
        return reservation.getDate();
    }

    public LocalTime getStartAt() {
        return reservation.getStartAt();
    }

    public String getStatusDisplayName() {
        return reservation.getStatusDisplayName();
    }
}
