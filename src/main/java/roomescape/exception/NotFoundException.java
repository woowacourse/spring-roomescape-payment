package roomescape.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("해당 리소스를 찾을 수 없습니다.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
