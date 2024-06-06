package roomescape.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.model.ReservationWithPaymentInfo;
import roomescape.model.WaitingWithRank;

import java.time.LocalDate;
import java.time.LocalTime;

public class MemberReservationResponse {

    private Long id;
    private String theme;
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private String status;
    private String paymentKey;
    private Long amount;

    public MemberReservationResponse(ReservationWithPaymentInfo reservation) {
        this.id = reservation.getReservation().getId();
        this.theme = reservation.getReservation().getTheme().getName();
        this.date = reservation.getReservation().getDate();
        this.time = reservation.getReservation().getTime().getStartAt();
        this.status = "예약";
        this.paymentKey = reservation.getPaymentInfo().getPaymentKey();
        this.amount = reservation.getPaymentInfo().getAmount();
    }

    public MemberReservationResponse(WaitingWithRank waitingWithRank) {
        this.id = waitingWithRank.getWaiting().getId();
        this.theme = waitingWithRank.getWaiting().getTheme().getName();
        this.date = waitingWithRank.getWaiting().getDate();
        this.time = waitingWithRank.getWaiting().getTime().getStartAt();
        this.status = "%d번째 예약대기".formatted(waitingWithRank.getRank());
        this.paymentKey = null;
        this.amount = null;
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
