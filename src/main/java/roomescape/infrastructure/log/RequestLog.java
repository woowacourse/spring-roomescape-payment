package roomescape.infrastructure.log;

import java.util.Arrays;
import java.util.Map;

public record RequestLog(
        String httpMethod,
        String uri,
        String className,
        String methodName,
        Object[] args,
        Map<String, String> params
) implements LogEntry {

    @Override
    public String toLogMessage() {
        return "[REQUEST] %s %s -> %s.%s | args=%s | params=%s".formatted(
                httpMethod, uri, className, methodName, Arrays.toString(args), params
        );
    }
}
