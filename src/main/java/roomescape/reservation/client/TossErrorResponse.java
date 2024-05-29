package roomescape.reservation.client;

public record TossErrorResponse(
        String code,
        String message,
        String data
) {
}
