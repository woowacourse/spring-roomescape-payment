package roomescape.advice.exception;

import org.springframework.http.ProblemDetail;

public class RoomEscapeException extends RuntimeException {
    private final ProblemDetail problemDetail;

    public RoomEscapeException(String message, ExceptionTitle title) {
        super(message);
        problemDetail = ProblemDetail.forStatusAndDetail(title.getStatusCode(), message);
        problemDetail.setTitle(title.getTitle());
    }

    public RoomEscapeException(String message, Throwable cause, ExceptionTitle title) {
        super(message, cause);
        problemDetail = ProblemDetail.forStatusAndDetail(title.getStatusCode(), message);
        problemDetail.setTitle(title.getTitle());
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }

    public int getStatus() {
        return problemDetail.getStatus();
    }
}
