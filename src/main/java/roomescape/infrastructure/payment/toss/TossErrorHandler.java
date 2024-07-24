package roomescape.infrastructure.payment.toss;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TossErrorHandler {

    private TossErrorHandler() {
    }

    public static HttpStatusCode covertStatusCode(final HttpStatusCode statusCode, final String errorCode) {
        boolean isServerError = Arrays.stream(TossSeverErrorCode.values())
                .anyMatch(tossSeverErrorCode -> tossSeverErrorCode.name().equals(errorCode));
        if (isServerError) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return statusCode;
    }
}
