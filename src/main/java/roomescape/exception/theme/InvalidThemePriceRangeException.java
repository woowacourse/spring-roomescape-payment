package roomescape.exception.theme;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class InvalidThemePriceRangeException extends RoomescapeException {
    public InvalidThemePriceRangeException() {
        super("유효하지 않은 테마 가격입니다. 가격은 0원 이상이여야 합니다.", HttpStatus.BAD_REQUEST);
    }
}
