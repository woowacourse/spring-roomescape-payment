package roomescape.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message, LocalDateTime timeStamp) {
}
