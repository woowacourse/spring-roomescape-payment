package roomescape.common.log.message;


import java.util.Map;
import org.springframework.http.ResponseEntity;
import roomescape.common.log.context.RequestContext;

public interface LogMessageProvider {

    String getRequestLog(
            final RequestContext requestContext,
            final Map<String, Object> handlerArguments
    );

    String getResponseLog(
            final RequestContext requestContext,
            final ResponseEntity<?> response
    );
}
