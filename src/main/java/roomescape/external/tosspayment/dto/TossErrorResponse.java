package roomescape.external.tosspayment.dto;

public record TossErrorResponse(
        String code,
        String message,
        Object data
) {
}
