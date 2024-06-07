package roomescape.service.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationwaiting.ReservationWaitingWithRank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class ReservationMineResponse {
    private static final String BOOKED_MESSAGE = "예약";
    private static final String PAYMENT_WAITING_MESSAGE = "결제 대기";
    private static final String WAITING_MESSAGE = "%d번째 예약대기";

    private final Long reservationId;
    private final String theme;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;
    private final String paymentKey;
    private final Integer amount;

    public ReservationMineResponse(Long reservationId, String theme, LocalDate date, LocalTime time, String status, String paymentKey, Integer amount) {
        this.reservationId = reservationId;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public static ReservationMineResponse ofReservationPayment(Reservation reservation, Optional<Payment> reservationPayment) {
        if (reservationPayment.isPresent()) {
            Payment payment = reservationPayment.get();
            return new ReservationMineResponse(reservation.getId(),
                    reservation.getTheme().getName().getName(),
                    reservation.getDate(),
                    reservation.getTime().getStartAt(),
                    BOOKED_MESSAGE,
                    payment.getPaymentKey(),
                    payment.getAmount()
            );
        }
        return new ReservationMineResponse(reservation.getId(),
                reservation.getTheme().getName().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                PAYMENT_WAITING_MESSAGE,
                "",
                0
        );
    }

    public ReservationMineResponse(ReservationWaitingWithRank waitingWithRank) {
        this(waitingWithRank.getWaiting().getReservation().getId(),
                waitingWithRank.getWaiting().getReservation().getTheme().getName().getName(),
                waitingWithRank.getWaiting().getReservation().getDate(),
                waitingWithRank.getWaiting().getReservation().getTime().getStartAt(),
                String.format(WAITING_MESSAGE, waitingWithRank.getRank()),
                "",
                0
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

    public Integer getAmount() {
        return amount;
    }
}
