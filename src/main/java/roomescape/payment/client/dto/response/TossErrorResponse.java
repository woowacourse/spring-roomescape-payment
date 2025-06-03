package roomescape.payment.client.dto.response;

public record TossErrorResponse(
        String code,
        String message
) {
}
