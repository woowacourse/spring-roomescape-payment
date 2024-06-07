package roomescape.service.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationWithPayment;
import roomescape.domain.reservationwaiting.ReservationWaitingWithRank;

public class ReservationMineResponse {
    private final Long reservationId;
    private final String theme;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;
    private final String paymentKey;
    private final String totalAmount;

    public ReservationMineResponse(Long reservationId, String theme, LocalDate date, LocalTime time, String status,
                                   String paymentKey, String totalAmount) {
        this.reservationId = reservationId;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public ReservationMineResponse(ReservationWithPayment reservationWithPayment) {
        this(reservationWithPayment.getReservation().getId(),
                reservationWithPayment.getReservation().getTheme().getName().getName(),
                reservationWithPayment.getReservation().getDate(),
                reservationWithPayment.getReservation().getReservationTime().getStartAt(),
                ReservationStatus.BOOKED.getDescription(),
                reservationWithPayment.getReservationPayment().getInfo().getPaymentKey(),
                reservationWithPayment.getReservationPayment().getInfo().getTotalAmountWithCurrency()
        );
    }

    public ReservationMineResponse(ReservationWaitingWithRank waitingWithRank) {
        this(waitingWithRank.getWaiting().getReservation().getId(),
                waitingWithRank.getWaiting().getReservation().getTheme().getName().getName(),
                waitingWithRank.getWaiting().getReservation().getDate(),
                waitingWithRank.getWaiting().getReservation().getReservationTime().getStartAt(),
                String.format(ReservationStatus.WAITING.getDescription(), waitingWithRank.getRank()),
                null,
                null
        );
    }

    public LocalDateTime retrieveDateTime() {
        return LocalDateTime.of(date, time);
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}
