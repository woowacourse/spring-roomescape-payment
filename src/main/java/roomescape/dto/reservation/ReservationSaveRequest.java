package roomescape.dto.reservation;

public record ReservationSaveRequest(
        String date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount
) {
}
