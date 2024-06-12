package roomescape.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Theme;

public class ReservationWithRankAndPayment {

    @Schema(description = "예약 ID")
    private final Reservation reservation;

    @Schema(description = "대기 순서")
    private final Long rank;

    @Schema(description = "결제 정보")
    private final Payment payment;

    public ReservationWithRankAndPayment(Reservation reservation, Long rank, Payment payment) {
        this.reservation = reservation;
        this.rank = rank;
        this.payment = payment;
    }

    public long getId() {
        return reservation.getId();
    }

    public LocalDate getDate() {
        return reservation.getDate();
    }

    public LocalTime getTime() {
        return reservation.getTime();
    }

    public Theme getTheme() {
        return reservation.getTheme();
    }

    public String getStatusMessage() {
        return reservation.getStatus().makeStatusMessage(rank);
    }

    public String getPaymentKey() {
        if (payment == null) {
            return null;
        }
        return payment.getPaymentKey();
    }

    public Long getPaymentAmount() {
        if (payment == null) {
            return null;
        }
        return payment.getAmount();
    }
}
