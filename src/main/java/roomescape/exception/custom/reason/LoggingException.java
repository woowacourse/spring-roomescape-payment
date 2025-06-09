package roomescape.exception.custom.reason;

import java.util.Map;

public class LoggingException extends RuntimeException {

    private final String eventCode;
    private final Map<String, Object> parameters;

    public LoggingException(String message, String eventCode, Map<String, Object> parameters) {
        super(message);
        this.eventCode = eventCode;
        this.parameters = parameters;
    }

    public String getEventCode() {
        return eventCode;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
