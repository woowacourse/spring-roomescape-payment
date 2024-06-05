package roomescape.exception;

public record PaymentErrorResponse(String code, String message) {

    public boolean isInvalid() {
        return code == null || message == null;
    }
}
