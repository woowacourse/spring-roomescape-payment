package roomescape.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    int getStatusValue();

    HttpStatus getStatus();

    String getMessage();

    String name();
}
