package roomescape.reservation.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.WaitingWithRank;

@Schema(description = "내 예약 응답")
public record MyReservationResponse(
        @Schema(description = "예약 ID 또는 대기 ID")
        Long id,

        @Schema(description = "테마 이름")
        String theme,

        @Schema(description = "예약 날짜")
        LocalDate date,

        @Schema(description = "예약 시간")
        LocalTime time,

        @Schema(description = "예약 상태. '예약' 또는 'N번째 예약 대기'")
        String status,

        @Schema(description = "결제 키. 예약이 아닌 대기일 경우 null")
        String paymentKey,

        @Schema(description = "결제 금액. 예약이 아닌 대기일 경우 null")
        BigDecimal amount
) {
    public static final String RESERVED = "예약";
    public static final String WAITING = "번째 예약 대기";

    public static List<MyReservationResponse> of(
            List<Reservation> reservations,
            List<WaitingWithRank> waitings,
            Map<Long, Payment> payments) {
        List<MyReservationResponse> reservationResponses = reservations.stream()
                .map(reservation -> {
                    Payment payment = payments.get(reservation.getId());
                    String paymentKey = null;
                    BigDecimal amount = null;
                    if (payment != null) {
                        paymentKey = payment.getPaymentKey().getKey();
                        amount = payment.getAmount();
                    }
                    return new MyReservationResponse(
                            reservation.getId(),
                            reservation.getTheme().getName(),
                            reservation.getDate(),
                            reservation.getTime().getStartAt(),
                            RESERVED,
                            paymentKey,
                            amount
                    );
                })
                .toList();

        List<MyReservationResponse> waitingResponses = waitings.stream()
                .map(waiting ->
                        new MyReservationResponse(
                                waiting.getId(),
                                waiting.getTheme().getName(),
                                waiting.getDate(),
                                waiting.getTime().getStartAt(),
                                waiting.getRank() + WAITING,
                                null,
                                null)
                )
                .toList();

        return Stream.concat(reservationResponses.stream(), waitingResponses.stream())
                .collect(Collectors.toList());

    }


}
