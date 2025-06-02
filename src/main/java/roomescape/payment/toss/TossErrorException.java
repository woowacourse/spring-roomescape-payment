package roomescape.payment.toss;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import roomescape.exception.BadRequestException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InternalServerException;
import roomescape.exception.NotFoundException;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.UnauthorizedException;

public enum TossErrorException {

    PROVIDER_ERROR((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_API_KEY((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_AUTHORIZE_AUTH(
            (tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    UNAUTHORIZED_KEY((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INCORRECT_BASIC_AUTH_FORMAT(
            (tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_UNREGISTERED_SUBMALL((tossError) -> new BadRequestException(tossError.code(), "안심클릭이나 ISP 결제가 필요합니다")),
    UNMAPPED(null);

    private static final Map<HttpStatus, BiFunction<String, String, RoomEscapeException>> EXCEPTION_FACTORY = Map.of(
            HttpStatus.BAD_REQUEST, BadRequestException::new,
            HttpStatus.UNAUTHORIZED, UnauthorizedException::new,
            HttpStatus.FORBIDDEN, ForbiddenException::new,
            HttpStatus.NOT_FOUND, NotFoundException::new,
            HttpStatus.INTERNAL_SERVER_ERROR, InternalServerException::new
    );
    private final Function<TossPaymentErrorResponse, RoomEscapeException> function;

    TossErrorException(Function<TossPaymentErrorResponse, RoomEscapeException> function) {
        this.function = function;
    }

    public static RoomEscapeException createException(TossPaymentErrorResponse errorResponse, HttpStatus statusCode) {
        TossErrorException tossErrorType = findTossErrorType(errorResponse);
        if (tossErrorType.isUnMappedException()) {
            BiFunction<String, String, RoomEscapeException> biFunction = EXCEPTION_FACTORY.get(statusCode);
            return biFunction.apply(errorResponse.code(), errorResponse.message());
        }
        return tossErrorType.function.apply(errorResponse);
    }

    private static TossErrorException findTossErrorType(TossPaymentErrorResponse errorResponse) {
        return Arrays.stream(TossErrorException.values())
                .filter(tossErrorException -> Objects.equals(tossErrorException.name(), errorResponse.code()))
                .findAny()
                .orElse(UNMAPPED);
    }

    private boolean isUnMappedException() {
        return this == UNMAPPED;
    }
}
