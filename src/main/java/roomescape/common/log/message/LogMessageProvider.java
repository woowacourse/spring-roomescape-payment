package roomescape.common.log.message;


import roomescape.common.log.entry.ErrorLogEntry;
import roomescape.common.log.entry.RequestLogEntry;
import roomescape.common.log.entry.ResponseLogEntry;

public interface LogMessageProvider {

    String getRequestLog(final RequestLogEntry requestLogEntry);

    String getResponseLog(final ResponseLogEntry responseLogEntry);

    String getErrorLog(final ErrorLogEntry errorLogEntry);
}
