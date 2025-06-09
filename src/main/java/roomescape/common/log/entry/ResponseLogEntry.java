package roomescape.common.log.entry;

import org.springframework.http.ResponseEntity;
import roomescape.common.log.context.RequestContext;

public record ResponseLogEntry(
        RequestContext requestContext,
        ResponseEntity<?> response
) {
}
