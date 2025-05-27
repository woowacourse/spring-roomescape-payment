package roomescape.reservation.infrastructure;

public class TossPaymentException extends RuntimeException {

    private final String code;

    public TossPaymentException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
