package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Schedule;
import roomescape.waiting.domain.Waiting;

public record MyReservationResponse(Long id,
                                    String themeName,
                                    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                    @JsonFormat(pattern = "HH:mm") LocalTime startAt,
                                    String status,
                                    Long waitingId,
                                    String paymentKey,
                                    BigDecimal amount) {
    private static final Map<Boolean, String> IS_ADMIN_STATUS_RESPONSE = Map.of(
            true, "어드민 예약 완료",
            false, "결제 대기");
    private static final String DONE_PAYMENT_FORMAT = "예약 완료";
    private static final String WAITING_STATUS_FORMAT = "%d번째 예약 대기";

    public static MyReservationResponse from(Reservation reservation) {
        Schedule schedule = reservation.getSchedule();
        String status = IS_ADMIN_STATUS_RESPONSE.get(reservation.isAdminReserved());
        return new MyReservationResponse(
                reservation.getId(),
                schedule.getTheme().getName(),
                schedule.getDate(),
                schedule.getTime().getStartAt(),
                status,
                null,
                null,
                null
        );
    }

    public static MyReservationResponse from(Reservation reservation, Payment payment) {
        Schedule schedule = reservation.getSchedule();
        return new MyReservationResponse(
                reservation.getId(),
                schedule.getTheme().getName(),
                schedule.getDate(),
                schedule.getTime().getStartAt(),
                DONE_PAYMENT_FORMAT,
                null,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationResponse from(Waiting waiting, Long order) {
        String status = WAITING_STATUS_FORMAT.formatted(order);
        Schedule schedule = waiting.getSchedule();
        return new MyReservationResponse(
                null,
                schedule.getTheme().getName(),
                schedule.getDate(),
                schedule.getTime().getStartAt(),
                status,
                waiting.getId(),
                null,
                null
        );
    }
}
