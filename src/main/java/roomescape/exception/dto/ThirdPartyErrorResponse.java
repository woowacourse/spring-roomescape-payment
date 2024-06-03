package roomescape.exception.dto;

public record ThirdPartyErrorResponse(
        String code,
        String message
) {
}
