package roomescape.reservation.client.errorcode;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public enum PaymentConfirmErrorCode {
    INVALID_API_KEY(RuntimeException::new),
    UNAUTHORIZED_KEY(RuntimeException::new),
    INCORRECT_BASIC_AUTH_FORMAT(RuntimeException::new),
    DEFAULT_ERROR_CODE(RuntimeException::new);

    private final Supplier<RuntimeException> exceptionSupplier;

    PaymentConfirmErrorCode(Supplier<RuntimeException> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
    }

    public RuntimeException getException() {
        return exceptionSupplier.get();
    }

    public static Optional<PaymentConfirmErrorCode> findByErrorCode(String errorCode) {
        return Arrays.stream(PaymentConfirmErrorCode.values())
                .filter(element -> element.name().equals(errorCode))
                .findFirst();
    }
}
