package roomescape.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.model.Reservation;
import roomescape.model.ReservationStatus;
import roomescape.model.ReservationWithPaymentInfo;
import roomescape.model.WaitingWithRank;

import java.time.LocalDate;
import java.time.LocalTime;

public class MemberReservationResponse {

    private static final String RESERVED = "예약";
    private static final String PAYMENT_WAITING = "결제 대기";
    private static final String WAITING = "%d번째 예약대기";

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
        this.status = getReservationStatus(reservation.getReservation());
        this.paymentKey = reservation.getPaymentInfo().getPaymentKey();
        this.amount = reservation.getPaymentInfo().getAmount();
    }

    public MemberReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.theme = reservation.getTheme().getName();
        this.date = reservation.getDate();
        this.time = reservation.getTime().getStartAt();
        this.status = getReservationStatus(reservation);
        this.paymentKey = null;
        this.amount = null;
    }

    public MemberReservationResponse(WaitingWithRank waitingWithRank) {
        this.id = waitingWithRank.getWaiting().getId();
        this.theme = waitingWithRank.getWaiting().getTheme().getName();
        this.date = waitingWithRank.getWaiting().getDate();
        this.time = waitingWithRank.getWaiting().getTime().getStartAt();
        this.status = WAITING.formatted(waitingWithRank.getRank());
        this.paymentKey = null;
        this.amount = null;
    }

    private String getReservationStatus(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            return RESERVED;
        }
        return PAYMENT_WAITING;
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
