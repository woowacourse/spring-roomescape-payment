package roomescape.config;

import java.util.Set;

public class RestClientErrorResponse {
    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private String code;
    private String message;

    public RestClientErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    protected RestClientErrorResponse() {
    }

    public boolean isInvisibleError() {
        return INVISIBLE_CLIENT_ERROR_CODE.contains(code);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
