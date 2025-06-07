package roomescape.infrastructure.log;

import java.time.LocalDateTime;

public record InfoLog(
        Long identifier,
        String className,
        String methodName,
        LocalDateTime timestamp
) implements LogEntry {

    @Override
    public String toLogMessage() {
        return "[INFO] %s.%s (identifier: %d), %s".formatted(className, methodName, identifier, timestamp);
    }
}
