package roomescape.reservationTime.adaptor;

import org.springframework.stereotype.Component;
import roomescape.reservationTime.docs.*;
import roomescape.reservationTime.dto.request.ReservationTimeRequest;
import roomescape.reservationTime.dto.request.TimeConditionRequest;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.reservationTime.dto.response.TimeConditionResponse;

@Component
public class ReservationTimeApiAdaptor {

    public ReservationTimeRequest toReservationTimeRequest(ReservationTimeRequestDocs dto) {
        return dto.toReservationTimeRequest();
    }

    public TimeConditionRequest toTimeConditionRequest(TimeConditionRequestDocs dto) {
        return dto.toTimeConditionRequest();
    }

    public ReservationTimeResponseDocs toReservationTimeResponseDocs(ReservationTimeResponse response) {
        return ReservationTimeResponseDocs.from(response);
    }

    public TimeConditionResponseDocs toTimeConditionResponseDocs(TimeConditionResponse response) {
        return TimeConditionResponseDocs.from(response);
    }
} 