package roomescape.domain.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.domain.reservation.domain.Reservation;

public final class MineReservationResponse {

    private final Long id;
    private final LocalDate date;
    private final String themeName;
    private final LocalTime startAt;
    private String paymentKey;
    private Long amount;

    public MineReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.date = reservation.getDate();
        this.themeName = reservation.getTheme().getName();
        this.startAt = reservation.getReservationTime().getStartAt();
        if (reservation.getPayment() != null) {
            this.paymentKey = reservation.getPayment().getPaymentKey();
            this.amount = reservation.getPayment().getAmount();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getThemeName() {
        return themeName;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MineReservationResponse that = (MineReservationResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(date, that.date)
                && Objects.equals(themeName, that.themeName) && Objects.equals(startAt, that.startAt)
                && Objects.equals(paymentKey, that.paymentKey) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, themeName, startAt, paymentKey, amount);
    }
}
