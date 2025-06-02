package roomescape.common.exception;

public class NotFoundException extends CustomException {

    public NotFoundException() {
        super("요청하신 자원을 찾을 수 없습니다.", GenaralErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, GenaralErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message, GenaralErrorCode genaralErrorCode) {
        super(message, genaralErrorCode);
    }
}
