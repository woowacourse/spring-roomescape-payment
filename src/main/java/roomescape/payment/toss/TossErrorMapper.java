package roomescape.payment.toss;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import roomescape.exception.BadRequestException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InternalServerException;
import roomescape.exception.NotFoundException;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.UnauthorizedException;

@Component
public class TossErrorMapper {

    private static final Map<HttpStatus, BiFunction<String, String, RoomEscapeException>> DEFAULT_EXCEPTION_FACTORY = Map.of(
            HttpStatus.BAD_REQUEST, BadRequestException::new,
            HttpStatus.UNAUTHORIZED, UnauthorizedException::new,
            HttpStatus.FORBIDDEN, ForbiddenException::new,
            HttpStatus.NOT_FOUND, NotFoundException::new,
            HttpStatus.INTERNAL_SERVER_ERROR, InternalServerException::new
    );

    public RoomEscapeException createException(TossPaymentErrorResponse errorResponse, HttpStatus statusCode) {
        Optional<RoomEscapeException> tossExceptionOptional = MappedTossError.createException(errorResponse);
        if (tossExceptionOptional.isEmpty()) {
            BiFunction<String, String, RoomEscapeException> biFunction = DEFAULT_EXCEPTION_FACTORY.get(statusCode);
            return biFunction.apply(errorResponse.code(), errorResponse.message());
        }
        return tossExceptionOptional.get();
    }
}
