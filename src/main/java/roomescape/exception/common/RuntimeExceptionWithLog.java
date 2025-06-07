package roomescape.exception.common;

public class RuntimeExceptionWithLog extends RuntimeException{

    private final String logMessage;

    public RuntimeExceptionWithLog(String message, String logMessage) {
        super(message);
        this.logMessage = logMessage;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
