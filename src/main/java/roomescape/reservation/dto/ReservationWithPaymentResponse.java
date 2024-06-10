package roomescape.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public class ReservationWithPaymentResponse {
    private final Long id;
    private final Theme theme;
    private final LocalDate date;
    private final ReservationTime time;
    private final ReservationStatus status;
    private final String paymentKey;
    private final BigDecimal totalAmount;

    public ReservationWithPaymentResponse(
            Long id,
            Theme theme,
            LocalDate date,
            ReservationTime time,
            ReservationStatus status,
            String paymentKey,
            BigDecimal totalAmount
    ) {
        this.id = id;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
