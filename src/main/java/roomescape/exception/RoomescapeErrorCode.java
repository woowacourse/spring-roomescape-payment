package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public interface RoomescapeErrorCode {
    HttpStatusCode httpStatusCode();
    String message();
}
