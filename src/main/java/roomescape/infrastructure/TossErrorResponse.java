package roomescape.infrastructure;

import org.springframework.http.HttpStatusCode;

public class TossErrorResponse extends PaymentErrorResponse {
    private final String code;
    private final String message;

    public TossErrorResponse(HttpStatusCode httpStatusCode, String code, String message) {
        super(httpStatusCode);
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
