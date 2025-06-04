package roomescape.common.dto.response;

public record ExceptionResponse(int status, String message, String path) {
}
