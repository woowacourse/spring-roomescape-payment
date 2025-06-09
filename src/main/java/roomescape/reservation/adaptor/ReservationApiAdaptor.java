package roomescape.reservation.adaptor;

import org.springframework.stereotype.Component;
import roomescape.reservation.docs.*;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.MyReservationAndWaitingResponse;

@Component
public class ReservationApiAdaptor {

    public ReservationRequest toReservationRequest(ReservationRequestDocs dto) {
        return dto.toReservationRequest();
    }

    public ReservationConditionRequest toReservationConditionRequest(ReservationConditionRequestDocs dto) {
        return dto.toReservationConditionRequest();
    }

    public ReservationResponseDocs toReservationResponseDocs(ReservationResponse response) {
        return ReservationResponseDocs.from(response);
    }

    public MyReservationResponseDocs toMyReservationResponseDocs(MyReservationResponse response) {
        return MyReservationResponseDocs.from(response);
    }

    public MyReservationAndWaitingResponseDocs toMyReservationAndWaitingResponseDocs(MyReservationAndWaitingResponse response) {
        return MyReservationAndWaitingResponseDocs.from(response);
    }
} 