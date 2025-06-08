package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import org.springframework.lang.Nullable;
import roomescape.domain.reservation.ReservationDetail;
import roomescape.domain.reservation.ReservationWithOrder;

public record UserReservationResponse(
    long id,
    LocalDate date,
    TimeSlotResponse time,
    ThemeResponse theme,
    String status,
    @Nullable String paymentKey,
    @Nullable Long amount
) {

    public static UserReservationResponse from(final ReservationDetail detail) {
        var reservation = detail.waiting().reservation();
        return new UserReservationResponse(
            reservation.id(),
            reservation.date(),
            TimeSlotResponse.from(reservation.timeSlot()),
            ThemeResponse.from(reservation.theme()),
            writeDescription(detail.waiting()),
            writePaymentKey(detail),
            detail.amount()
        );
    }

    private static String writeDescription(final ReservationWithOrder waiting) {
        if (waiting.isWaiting()) {
            return waiting.order() + "번째 예약 대기";
        }
        var reservation = waiting.reservation();
        return reservation.status().description();
    }

    private static String writePaymentKey(final ReservationDetail detail) {
        if (detail.paymentKey() == null) {
            return null;
        }
        return detail.paymentKey().value();
    }

    public static List<UserReservationResponse> from(final List<ReservationDetail> details) {
        return details.stream()
            .map(UserReservationResponse::from)
            .toList();
    }
}
