package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class ThemeNameLengthException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE_FORMAT = "테마 이름은 %d자를 넘길 수 없습니다.";

    public ThemeNameLengthException(int length) {
        super(String.format(MESSAGE_FORMAT, length), STATUS);
    }
}
