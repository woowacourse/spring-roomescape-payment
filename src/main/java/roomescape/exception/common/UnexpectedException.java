package roomescape.exception.common;

public class UnexpectedException extends Exception {

    private final String logMessage;

    public UnexpectedException(Exception rootException) {
        super("[예외 발생] " + rootException.getMessage());
        this.logMessage = buildLogMessage(rootException);
    }

    public String getLogMessage() {
        return logMessage;
    }

    private String buildLogMessage(Exception rootException) {
        Throwable cause = rootException.getCause();
        if (cause == null) {
            return getMessage();
        }
        return "[예외 발생] " + getMessage() + ", rootCause : " + cause.getMessage();
    }
}
