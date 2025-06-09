package roomescape.common.log.message.json;

import roomescape.common.log.message.RequestInfo;

record ErrorLogEntry(
        RequestInfo requestInfo,
        ErrorDetail detail
) {

    static ErrorLogEntry of(
            final RequestInfo requestInfo,
            final Throwable throwable
    ) {
        return new ErrorLogEntry(
                requestInfo,
                ErrorDetail.fromThrowable(throwable)
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
