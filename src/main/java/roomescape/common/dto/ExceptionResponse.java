package roomescape.common.dto;

public record ExceptionResponse(int status, String message, String path) {
}
