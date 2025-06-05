package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Waiting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public record MyReservationResponse(
        Long id,

        String theme,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        String status,

        boolean isWaiting,

        String paymentKey,

        BigDecimal amount
) {

    public static MyReservationResponse fromAdminReservation(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getName(),
                false,
                null,
                null
        );
    }

    public static MyReservationResponse fromMemberReservation(Payment payment) {
        Reservation reservation = payment.getReservation();
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getName(),
                false,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationResponse from(Waiting waiting) {
        return new MyReservationResponse(
                waiting.getId(),
                waiting.getReservationInfo().getTheme().getName(),
                waiting.getReservationInfo().getDate(),
                waiting.getReservationInfo().getTime().getStartAt(),
                waiting.getRank() + "번째 예약대기",
                true,
                null,
                null
        );
    }

    public static List<MyReservationResponse> from(
            List<Reservation> reservations,
            List<Payment> payments,
            List<Waiting> waitings
    ) {
        return Stream.of(
                reservations.stream().map(MyReservationResponse::fromAdminReservation),
                payments.stream().map(MyReservationResponse::fromMemberReservation),
                waitings.stream().map(MyReservationResponse::from)
        )
        .flatMap(stream -> stream)
        .sorted(Comparator.comparing(MyReservationResponse::date)
                .thenComparing(MyReservationResponse::time))
        .toList();
    }
}
