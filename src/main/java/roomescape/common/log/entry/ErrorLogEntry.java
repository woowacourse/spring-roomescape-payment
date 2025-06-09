package roomescape.common.log.entry;

import org.springframework.http.ResponseEntity;
import roomescape.common.log.context.RequestContext;

public record ErrorLogEntry(
        RequestContext requestContext,
        ErrorDetail detail,
        ResponseEntity<?> response
) {

    public static ErrorLogEntry withThrowable(
            final RequestContext requestContext,
            final Throwable throwable,
            final ResponseEntity<?> response
    ) {
        return new ErrorLogEntry(
                requestContext,
                ErrorDetail.fromThrowable(throwable),
                response
        );
    }

    public static ErrorLogEntry withoutThrowable(
            final RequestContext requestContext,
            final ResponseEntity<?> response
    ) {
        return new ErrorLogEntry(
                requestContext,
                null,
                response
        );
    }

    private record ErrorDetail(
            String message,
            String className,
            String methodName,
            int lineNumber
    ) {

        static ErrorDetail fromThrowable(
                final Throwable throwable
        ) {
            StackTraceElement finalStackTraceElement = throwable.getStackTrace()[0];
            return new ErrorDetail(
                    throwable.getMessage(),
                    finalStackTraceElement.getClassName(),
                    finalStackTraceElement.getMethodName(),
                    finalStackTraceElement.getLineNumber()
            );
        }
    }
}
