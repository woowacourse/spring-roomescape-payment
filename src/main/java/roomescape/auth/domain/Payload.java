package roomescape.auth.domain;

import java.util.function.Supplier;

import roomescape.exception.ErrorType;
import roomescape.exception.RoomescapeException;

public class Payload<T> {
    private final T body;

    private final Supplier<Boolean> validator;

    public Payload(T body, Supplier<Boolean> validator) {
        this.body = body;
        this.validator = validator;
    }

    public T getValue() {

        if (Boolean.FALSE.equals(validator.get())) {
            throw new RoomescapeException(ErrorType.TOKEN_PAYLOAD_EXTRACTION_FAILURE);
        }
        return body;
    }
}
