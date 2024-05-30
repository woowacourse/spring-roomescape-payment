package roomescape.core.dto.exception;

public class HttpExceptionResponse {
    private String code;
    private String message;
    private String data;

    public HttpExceptionResponse() {
    }

    public HttpExceptionResponse(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
