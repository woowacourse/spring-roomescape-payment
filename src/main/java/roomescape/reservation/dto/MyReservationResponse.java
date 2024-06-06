package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.model.Payment;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.WaitingWithRank;

public class MyReservationResponse {

    private final Long id;
    private final String theme;
    private final LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private final LocalTime time;
    private final String status;
    private String paymentKey;
    private Long amount;

    public MyReservationResponse(final Reservation reservation, final Payment payment) {
        this.id = reservation.getId();
        this.theme = reservation.getTheme().getName().getValue();
        this.date = reservation.getDate().getValue();
        this.time = reservation.getTime().getStartAt();
        this.status = "예약";
        this.paymentKey = payment.getPaymentKey();
        this.amount = payment.getTotalAmount();
    }

    public MyReservationResponse(final WaitingWithRank waitingWithRank) {
        this.id = waitingWithRank.getWaiting().getId();
        this.theme = waitingWithRank.getWaiting().getReservation().getTheme().getName().getValue();
        this.date = waitingWithRank.getWaiting().getReservation().getDate().getValue();
        this.time = waitingWithRank.getWaiting().getReservation().getTime().getStartAt();
        this.status = waitingWithRank.getRank() + "번째 예약대기";
    }

    public Long getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
