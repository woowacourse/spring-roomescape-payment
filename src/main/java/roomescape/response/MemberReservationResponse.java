package roomescape.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.model.Reservation;
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
    private String paymentkey;
    private Long amount;

    public MemberReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.theme = reservation.getTheme().getName();
        this.date = reservation.getDate();
        this.time = reservation.getTime().getStartAt();
        this.status = "예약";
        this.paymentkey = reservation.getPayment().getPaymentKey();
        this.amount = reservation.getPayment().getAmount().longValue();
    }

    public MemberReservationResponse(WaitingWithRank waitingWithRank) {
        this.id = waitingWithRank.getWaiting().getId();
        this.theme = waitingWithRank.getWaiting().getTheme().getName();
        this.date = waitingWithRank.getWaiting().getDate();
        this.time = waitingWithRank.getWaiting().getTime().getStartAt();
        this.status = "%d번째 예약대기".formatted(waitingWithRank.getRank());
        this.paymentkey = "";
        this.amount = 0L;
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
        return paymentkey;
    }

    public Long getAmount() {
        return amount;
    }
}
