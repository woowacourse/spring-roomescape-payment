package roomescape.payment;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public enum TossErrorType {

    PROVIDER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    INVALID_API_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    INVALID_AUTHORIZE_AUTH(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    UNAUTHORIZED_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    INCORRECT_BASIC_AUTH_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    INVALID_UNREGISTERED_SUBMALL(HttpStatus.BAD_REQUEST, "안심클릭이나 ISP 결제가 필요합니다"),
    NONE(null, null);

    private final HttpStatus status;
    private final String message;

    TossErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static TossErrorType findByCode(String code) {
        return Arrays.stream(TossErrorType.values())
                .filter(tossErrorType -> Objects.equals(tossErrorType.name(), code))
                .findAny()
                .orElse(NONE);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
