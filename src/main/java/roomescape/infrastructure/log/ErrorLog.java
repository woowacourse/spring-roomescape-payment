package roomescape.infrastructure.log;

public record ErrorLog(
        String className,
        String methodName,
        String message,
        Throwable ex
) implements LogEntry {

    @Override
    public String toLogMessage() {
        return "[ERROR] %s.%s -> message=%s, exception=%s".formatted(
                className, methodName, message, ex != null ? ex.toString() : "null"
        );
    }
}
