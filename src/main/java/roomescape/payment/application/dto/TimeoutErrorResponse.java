package roomescape.payment.application.dto;

public class TimeoutErrorResponse {
    private String code;
    private String message;

    private TimeoutErrorResponse() {
    }

    public TimeoutErrorResponse(String code, String message) {
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
