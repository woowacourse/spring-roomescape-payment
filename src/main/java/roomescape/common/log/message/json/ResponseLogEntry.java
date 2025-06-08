package roomescape.common.log.message.json;

import org.springframework.http.ResponseEntity;
import roomescape.common.log.message.RequestInfo;

record ResponseLogEntry(
        RequestInfo requestInfo,
        ResponseEntity<?> response
) {
}
