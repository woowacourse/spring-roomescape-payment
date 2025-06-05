package roomescape.global.exception.dto;

public class ErrorResponse {

    private String code;
    private String message;

    private ErrorResponse() {
    }

    public ErrorResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
