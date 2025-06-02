package roomescape.common.exception;

public class PaymentException extends CustomException {

    public PaymentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    @Override
    public String getMessageForClient() {
        if (errorCode == PaymentErrorCode.CLIENT_ERROR) {
            return getMessage();
        }
        return super.getMessageForClient();
    }
}
