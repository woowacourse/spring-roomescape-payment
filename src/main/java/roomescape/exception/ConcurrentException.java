package roomescape.exception;

import org.springframework.dao.DataAccessException;

public class ConcurrentException extends DataAccessException {

    public ConcurrentException(String msg) {
        super(msg);
    }
}
