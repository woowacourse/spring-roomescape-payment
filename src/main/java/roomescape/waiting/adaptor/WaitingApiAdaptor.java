package roomescape.waiting.adaptor;

import org.springframework.stereotype.Component;
import roomescape.waiting.docs.*;
import roomescape.waiting.dto.request.WaitingRequest;
import roomescape.waiting.dto.response.WaitingResponse;

@Component
public class WaitingApiAdaptor {

    public WaitingRequest toWaitingRequest(WaitingRequestDocs dto) {
        return dto.toWaitingRequest();
    }

    public WaitingResponseDocs toWaitingResponseDocs(WaitingResponse response) {
        return WaitingResponseDocs.from(response);
    }
} 