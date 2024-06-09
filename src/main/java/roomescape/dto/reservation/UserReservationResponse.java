package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;

public record UserReservationResponse(
        Long id,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String paymentKey,
        BigDecimal amount,
        String status,
        Integer waitingOrder
) {

    private static final String RESERVED = "예약 완료";
    private static final String PAYMENT_PENDING = "결제 대기";
    private static final String WAITING = "예약 대기";

    public static UserReservationResponse createByContainPayment(Reservation reservation, Payment payment) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                payment.getPaymentKey(),
                payment.getAmount(),
                convertStatus(reservation.getStatus()),
                null
        );
    }

    public static UserReservationResponse createByContainWaitingOrder(Reservation reservation, int reservationOrder) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                null,
                null,
                convertStatus(reservation.getStatus()),
                reservationOrder
        );
    }

    public static UserReservationResponse create(Reservation reservation) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                null,
                null,
                convertStatus(reservation.getStatus()),
                null
        );
    }

    private static String convertStatus(Status status) {
        if (status == Status.RESERVED) {
            return RESERVED;
        }
        if (status == Status.PAYMENT_PENDING) {
            return PAYMENT_PENDING;
        }
        return WAITING;
    }
}
