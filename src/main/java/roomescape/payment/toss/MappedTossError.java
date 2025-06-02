package roomescape.payment.toss;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import roomescape.exception.BadRequestException;
import roomescape.exception.InternalServerException;
import roomescape.exception.RoomEscapeException;

@RequiredArgsConstructor
public enum MappedTossError {

    PROVIDER_ERROR((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_API_KEY((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_AUTHORIZE_AUTH(
            (tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    UNAUTHORIZED_KEY((tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INCORRECT_BASIC_AUTH_FORMAT(
            (tossError) -> new InternalServerException(tossError.code(), "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")),
    INVALID_UNREGISTERED_SUBMALL((tossError) -> new BadRequestException(tossError.code(), "안심클릭이나 ISP 결제가 필요합니다"));

    private final Function<TossPaymentErrorResponse, RoomEscapeException> function;

    public static Optional<RoomEscapeException> createException(TossPaymentErrorResponse tossError) {
        return Arrays.stream(MappedTossError.values())
                .filter(mappedTossError -> mappedTossError.name().equals(tossError.code()))
                .map(mappedTossError -> mappedTossError.function.apply(tossError))
                .findFirst();
    }
}
