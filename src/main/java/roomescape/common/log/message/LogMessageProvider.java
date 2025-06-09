package roomescape.common.log.message;


import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface LogMessageProvider {

    String getRequestLog(
            final RequestInfo requestInfo,
            final Map<String, Object> handlerArguments
    );

    String getResponseLog(
            final RequestInfo requestInfo,
            final ResponseEntity<?> response
    );

    String getErrorLog(
            final RequestInfo requestInfo,
            final Throwable throwable
    );
}
