package roomescape.exception;

import java.util.EnumSet;
import java.util.Set;

public enum ErrorTypeGroup {
    ADMIN(EnumSet.of(
            ErrorType.SECURITY_EXCEPTION,
            ErrorType.NOT_ALLOWED_PERMISSION_ERROR
    )),
    WAITING_RESERVATION(EnumSet.of(
            ErrorType.MEMBER_NOT_FOUND,
            ErrorType.MEMBER_RESERVATION_NOT_FOUND,
            ErrorType.NOT_A_WAITING_RESERVATION));

    Set<ErrorType> errorTypes;

    ErrorTypeGroup(final Set<ErrorType> errorTypes) {
        this.errorTypes = errorTypes;
    }

    public Set<ErrorType> getErrorTypes() {
        return errorTypes;
    }
}
