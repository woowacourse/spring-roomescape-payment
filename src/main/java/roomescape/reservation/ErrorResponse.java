package roomescape.reservation;

public record ErrorResponse(
        String code,
        String message
) {
}
