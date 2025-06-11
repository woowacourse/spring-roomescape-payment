package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class ThemeNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 테마입니다.";

    public ThemeNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
