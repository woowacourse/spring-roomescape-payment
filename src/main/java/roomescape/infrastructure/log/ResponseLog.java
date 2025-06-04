package roomescape.infrastructure.log;

public record ResponseLog(
        String className,
        String methodName,
        Object result,
        long duration
) implements LogEntry {

    @Override
    public String toLogMessage() {
        return "[RESPONSE] %s.%s -> returned=%s (%dms)".formatted(
                className, methodName, result, duration
        );
    }
}
