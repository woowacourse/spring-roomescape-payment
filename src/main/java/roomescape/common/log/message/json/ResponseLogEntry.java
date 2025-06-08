package roomescape.common.log.message.json;

import org.springframework.http.ResponseEntity;
import roomescape.common.log.context.RequestContext;

record ResponseLogEntry(
        RequestContext requestContext,
        ResponseEntity<?> response
) {
}
