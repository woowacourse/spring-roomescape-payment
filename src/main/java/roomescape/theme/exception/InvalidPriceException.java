package roomescape.theme.exception;

import roomescape.common.exception.BusinessException;

public class InvalidPriceException extends BusinessException {
    public InvalidPriceException() {
        super(ThemeErrorCode.INVALID_PRICE);
    }
}
