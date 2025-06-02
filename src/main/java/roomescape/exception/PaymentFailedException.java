package roomescape.exception;

public class PaymentFailedException extends RuntimeException {

    private final Cause cause;

    private PaymentFailedException(final Cause cause, final String message) {
        super(message);
        this.cause = cause;
    }

    public boolean causedByClient() {
        return cause == Cause.CLIENT_ERROR;
    }

    public boolean causedByServer() {
        return cause == Cause.SERVER_ERROR;
    }

    public boolean causedByExternalServer() {
        return cause == Cause.EXTERNAL_ERROR;
    }

    public static PaymentFailedException byClient(final String message) {
        return new PaymentFailedException(Cause.CLIENT_ERROR, message);
    }

    public static PaymentFailedException byServer() {
        return new PaymentFailedException(Cause.SERVER_ERROR, "일시적인 서버 오류로 결제에 실패했습니다.");
    }

    public static PaymentFailedException byExternalServer() {
        return new PaymentFailedException(Cause.EXTERNAL_ERROR, "선택한 결제 수단의 서버에서의 오류로 결제에 실패했습니다.");
    }

    private enum Cause {
        CLIENT_ERROR,
        SERVER_ERROR,
        EXTERNAL_ERROR
    }
}
