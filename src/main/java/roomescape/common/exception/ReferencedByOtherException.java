package roomescape.common.exception;

public class ReferencedByOtherException extends CustomException {

    public ReferencedByOtherException(String message) {
        super(message, GenaralErrorCode.CONFLICT);
    }

    public ReferencedByOtherException(String message, GenaralErrorCode genaralErrorCode) {
        super(message, genaralErrorCode);
    }
}
