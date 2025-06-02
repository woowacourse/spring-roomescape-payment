package roomescape.common.exception;

public class BadRequestException extends CustomException {

    public BadRequestException() {
        super("잘못된 입력입니다.", GenaralErrorCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, GenaralErrorCode.BAD_REQUEST);
    }
}
